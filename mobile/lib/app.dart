import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'core/router/app_router.dart';
import 'core/theme/app_theme.dart';

class IkindleApp extends ConsumerWidget {
  const IkindleApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final router = ref.watch(routerProvider);

    // 桌面端不使用 ScreenUtilInit，避免字体过大和布局拉伸
    if (Platform.isWindows || Platform.isMacOS || Platform.isLinux) {
      return MaterialApp.router(
        title: 'iKindle',
        debugShowCheckedModeBanner: false,
        theme: AppTheme.lightDesktop,
        darkTheme: AppTheme.darkDesktop,
        themeMode: ThemeMode.system,
        routerConfig: router,
        localizationsDelegates: const [
          ...GlobalMaterialLocalizations.delegates,
        ],
        supportedLocales: const [
          Locale('zh', 'CN'),
          Locale('en', 'US'),
        ],
      );
    }

    // 移动端继续使用 ScreenUtilInit
    return ScreenUtilInit(
      designSize: const Size(375, 812),
      minTextAdapt: true,
      splitScreenMode: true,
      builder: (context, child) {
        return MaterialApp.router(
          title: 'iKindle',
          debugShowCheckedModeBanner: false,
          theme: AppTheme.light,
          darkTheme: AppTheme.dark,
          themeMode: ThemeMode.system,
          routerConfig: router,
          localizationsDelegates: const [
            ...GlobalMaterialLocalizations.delegates,
          ],
          supportedLocales: const [
            Locale('zh', 'CN'),
            Locale('en', 'US'),
          ],
        );
      },
    );
  }
}
