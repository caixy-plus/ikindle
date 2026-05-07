import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:ikindle_mobile/core/providers/auth_provider.dart';
import 'package:ikindle_mobile/main.dart' as app;

const String kTestEmail = '691788300@qq.com';
const String kTestPassword = '26362663qq';
const String kPlatformClientId = 'a91c9e88f4b842eca985';
const String kRedirectUri = 'ikindle://oauth/callback';

class _T {
  static void log(String msg) {
    final ts = DateTime.now().toIso8601String().substring(11, 23);
    // ignore: avoid_print
    print('[IK-IT $ts] $msg');
  }
}

Future<void> clearStorage() async {
  final prefs = await SharedPreferences.getInstance();
  await prefs.clear();
}

Future<void> pumpApp(WidgetTester tester) async {
  app.main();
  await tester.pumpAndSettle(const Duration(seconds: 3));
}

Future<void> tapAndSettle(WidgetTester tester, Finder finder) async {
  await tester.tap(finder);
  await tester.pumpAndSettle();
}

Future<String> _fetchOAuthCode() async {
  final client = HttpClient();
  try {
    // 1. 平台用户登录拿 JWT
    final loginReq = await client.postUrl(
      Uri.parse('https://app.local.caixy.xin/api/v1/user/auth/login'),
    );
    loginReq.headers.contentType = ContentType.json;
    loginReq.add(utf8.encode(jsonEncode({
      'email': kTestEmail,
      'password': kTestPassword,
    })));
    final loginResp = await loginReq.close();
    final loginBody = await loginResp.transform(utf8.decoder).join();
    final loginJson = jsonDecode(loginBody) as Map<String, dynamic>;
    if (loginJson['code'] != 0) {
      throw StateError('平台登录失败: $loginBody');
    }
    final userJwt = (loginJson['data'] as Map)['accessToken'] as String;
    _T.log('平台登录成功');

    // 2. authorize 拿 code
    final authUri = Uri.parse(
      'https://app.local.caixy.xin/api/v1/oauth/authorize'
      '?client_id=$kPlatformClientId'
      '&redirect_uri=${Uri.encodeComponent(kRedirectUri)}'
      '&scope=read'
      '&state=integration-test',
    );
    final authReq = await client.getUrl(authUri);
    authReq.headers.add('Authorization', 'Bearer $userJwt');
    final authResp = await authReq.close();
    final authBody = await authResp.transform(utf8.decoder).join();
    final authJson = jsonDecode(authBody) as Map<String, dynamic>;
    if (authJson['code'] != 0) {
      throw StateError('authorize 失败: $authBody');
    }
    final code = (authJson['data'] as Map)['code'] as String;
    _T.log('拿到 OAuth code: ${code.substring(0, 8)}...');
    return code;
  } finally {
    client.close(force: true);
  }
}

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  group('OAuth 登录', () {
    setUp(clearStorage);

    testWidgets('通过统一授权页登录成功', (tester) async {
      await pumpApp(tester);

      // 未登录态应跳转到 OAuth 登录页
      expect(find.text('授权登录'), findsOneWidget);

      // 点击"账号密码登录"切换到本地登录
      final localLoginBtn = find.text('账号密码登录');
      expect(localLoginBtn, findsOneWidget);
      await tapAndSettle(tester, localLoginBtn);

      // 输入账号密码
      final textFields = find.byType(TextField);
      expect(textFields, findsAtLeast(2));
      await tester.enterText(textFields.at(0), 'admin');
      await tester.enterText(textFields.at(1), 'admin123');
      await tester.pumpAndSettle();

      final loginBtn = find.widgetWithText(ElevatedButton, '账号密码登录');
      await tapAndSettle(tester, loginBtn);
      await tester.pumpAndSettle(const Duration(seconds: 3));

      // 登录成功后应看到底部导航栏
      expect(find.byType(BottomNavigationBar), findsOneWidget);
      _T.log('本地账号登录成功');
    });

    testWidgets('OAuth code 兑换本地 token', (tester) async {
      await pumpApp(tester);

      // 直接通过 HTTP 拿 code，绕过 WebView
      final code = await _fetchOAuthCode();

      // 切换到账号密码登录页，以便拿到 Provider
      final localLoginBtn = find.text('账号密码登录');
      if (localLoginBtn.evaluate().isNotEmpty) {
        await tapAndSettle(tester, localLoginBtn);
      }

      // 通过 Provider 直接调用 oauthLogin
      // 注意: integration_test 中可以通过 tester 拿到 BuildContext
      final context = tester.element(find.byType(Scaffold).first);
      final container = ProviderScope.containerOf(context);
      await container.read(authStateProvider.notifier).oauthLogin(code);
      await tester.pumpAndSettle(const Duration(seconds: 3));

      final user = container.read(authStateProvider).valueOrNull;
      expect(user, isNotNull);
      expect(user!.email, kTestEmail);
      _T.log('OAuth 登录成功: ${user.username}');

      // 确认已在首页
      expect(find.byType(BottomNavigationBar), findsOneWidget);
    });
  });

  group('书架', () {
    setUp(clearStorage);

    testWidgets('书架 tab 切换不报错', (tester) async {
      await pumpApp(tester);

      // 本地登录
      final localLoginBtn = find.text('账号密码登录');
      if (localLoginBtn.evaluate().isNotEmpty) {
        await tapAndSettle(tester, localLoginBtn);
      }
      await tester.enterText(find.byType(TextField).at(0), 'admin');
      await tester.enterText(find.byType(TextField).at(1), 'admin123');
      await tapAndSettle(tester, find.widgetWithText(ElevatedButton, '账号密码登录'));
      await tester.pumpAndSettle(const Duration(seconds: 3));

      // 点击书架 tab
      final bookshelfTab = find.descendant(
        of: find.byType(BottomNavigationBar),
        matching: find.text('书架'),
      );
      await tapAndSettle(tester, bookshelfTab);

      // 验证 tab 存在
      expect(find.text('全部'), findsOneWidget);
      expect(find.text('等待中'), findsOneWidget);
      expect(find.text('已同步'), findsOneWidget);
      expect(find.text('待回执'), findsOneWidget);
      expect(find.text('已完成'), findsOneWidget);

      // 等待数据加载
      await tester.pumpAndSettle(const Duration(seconds: 2));

      // 不应显示报错（可能显示"暂无图书"）
      expect(find.textContaining('加载失败'), findsNothing);

      // 切换 tab
      await tapAndSettle(tester, find.text('等待中'));
      await tester.pumpAndSettle(const Duration(seconds: 1));
      expect(find.textContaining('加载失败'), findsNothing);

      await tapAndSettle(tester, find.text('已同步'));
      await tester.pumpAndSettle(const Duration(seconds: 1));
      expect(find.textContaining('加载失败'), findsNothing);

      _T.log('书架 tab 切换测试通过');
    });
  });

  group('图书详情', () {
    setUp(clearStorage);

    testWidgets('加入书架按钮可点击并有反馈', (tester) async {
      await pumpApp(tester);

      // 本地登录
      final localLoginBtn = find.text('账号密码登录');
      if (localLoginBtn.evaluate().isNotEmpty) {
        await tapAndSettle(tester, localLoginBtn);
      }
      await tester.enterText(find.byType(TextField).at(0), 'admin');
      await tester.enterText(find.byType(TextField).at(1), 'admin123');
      await tapAndSettle(tester, find.widgetWithText(ElevatedButton, '账号密码登录'));
      await tester.pumpAndSettle(const Duration(seconds: 3));

      // 等待图书加载
      await tester.pumpAndSettle(const Duration(seconds: 2));
      final bookCards = find.byType(Card);
      if (bookCards.evaluate().isEmpty) {
        _T.log('首页没有图书，跳过图书详情测试');
        return;
      }
      await tapAndSettle(tester, bookCards.first);
      await tester.pumpAndSettle(const Duration(seconds: 2));

      // 详情页应有"加入书架"按钮
      final addBtn = find.widgetWithText(ElevatedButton, '加入书架');
      expect(addBtn, findsOneWidget);

      // 点击加入书架
      await tapAndSettle(tester, addBtn);
      await tester.pumpAndSettle(const Duration(seconds: 3));

      // 应显示 SnackBar 反馈
      expect(find.textContaining('成功'), findsAtLeast(1));

      _T.log('加入书架按钮测试通过');
    });
  });

  group('我的页面', () {
    setUp(clearStorage);

    testWidgets('菜单项可点击', (tester) async {
      await pumpApp(tester);

      // 本地登录
      final localLoginBtn = find.text('账号密码登录');
      if (localLoginBtn.evaluate().isNotEmpty) {
        await tapAndSettle(tester, localLoginBtn);
      }
      await tester.enterText(find.byType(TextField).at(0), 'admin');
      await tester.enterText(find.byType(TextField).at(1), 'admin123');
      await tapAndSettle(tester, find.widgetWithText(ElevatedButton, '账号密码登录'));
      await tester.pumpAndSettle(const Duration(seconds: 3));

      // 点击"我的"tab
      final profileTab = find.descendant(
        of: find.byType(BottomNavigationBar),
        matching: find.text('我的'),
      );
      await tapAndSettle(tester, profileTab);

      // 验证菜单项存在
      expect(find.text('我的订单'), findsOneWidget);
      expect(find.text('充值'), findsOneWidget);
      expect(find.text('Kindle 设置'), findsOneWidget);
      expect(find.text('帮助反馈'), findsOneWidget);
      expect(find.text('关于'), findsOneWidget);
      expect(find.text('退出登录'), findsOneWidget);

      // 点击 Kindle 设置
      await tapAndSettle(tester, find.text('Kindle 设置'));
      await tester.pumpAndSettle();
      expect(find.text('Kindle 邮箱'), findsOneWidget);
      await tester.tap(find.text('关闭').last);
      await tester.pumpAndSettle();

      // 点击帮助反馈
      await tapAndSettle(tester, find.text('帮助反馈'));
      await tester.pumpAndSettle();
      expect(find.text('帮助与反馈'), findsOneWidget);
      await tester.tap(find.text('关闭').last);
      await tester.pumpAndSettle();

      // 点击关于
      await tapAndSettle(tester, find.text('关于'));
      await tester.pumpAndSettle();
      expect(find.text('iKindle'), findsAtLeast(1));
      // showAboutDialog 的关闭按钮在不同平台文本不同，找 TextButton
      final closeBtn = find.byType(TextButton).last;
      await tester.tap(closeBtn);
      await tester.pumpAndSettle();

      _T.log('我的页面菜单测试通过');
    });
  });
}
