import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:go_router/go_router.dart';
import '../../core/providers/auth_provider.dart';
import '../../core/theme/app_colors.dart';

class ProfilePage extends ConsumerWidget {
  const ProfilePage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authStateProvider);
    final user = authState.valueOrNull;
    final isDesktop = Platform.isWindows || Platform.isMacOS || Platform.isLinux;

    return Scaffold(
      appBar: AppBar(title: const Text('我的')),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: isDesktop
              ? const EdgeInsets.all(24)
              : EdgeInsets.all(16.w),
          child: Column(
            children: [
              _buildUserCard(user, isDesktop),
              SizedBox(height: isDesktop ? 24 : 16.h),
              _buildMenuCard(context, ref, isDesktop),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildUserCard(dynamic user, bool isDesktop) {
    return Container(
      padding: isDesktop
          ? const EdgeInsets.all(24)
          : EdgeInsets.all(16.w),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [AppColors.primary, AppColors.primaryDark],
        ),
        borderRadius: isDesktop
            ? const BorderRadius.all(Radius.circular(16))
            : BorderRadius.circular(12.r),
      ),
      child: Row(
        children: [
          CircleAvatar(
            radius: isDesktop ? 36 : 32.r,
            backgroundColor: Colors.white.withOpacity(0.3),
            child: Icon(
              Icons.person,
              size: isDesktop ? 36 : 32.r,
              color: Colors.white,
            ),
          ),
          SizedBox(width: isDesktop ? 20 : 16.w),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  user?.nickname ?? user?.username ?? '未登录',
                  style: TextStyle(
                    fontSize: isDesktop ? 20 : 18.sp,
                    fontWeight: FontWeight.bold,
                    color: Colors.white,
                  ),
                ),
                SizedBox(height: isDesktop ? 6 : 4.h),
                Text(
                  user?.email ?? '',
                  style: TextStyle(
                    fontSize: isDesktop ? 14 : 13.sp,
                    color: Colors.white.withOpacity(0.9),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildMenuCard(BuildContext context, WidgetRef ref, bool isDesktop) {
    final items = [
      ('我的订单', Icons.receipt_long, () => context.push('/orders')),
      ('充值', Icons.account_balance_wallet, () => context.push('/recharge')),
      ('Kindle 设置', Icons.settings, () => _showKindleSettings(context, isDesktop)),
      ('帮助反馈', Icons.help_outline, () => _showHelpFeedback(context, isDesktop)),
      ('关于', Icons.info_outline, () => _showAbout(context, isDesktop)),
      ('退出登录', Icons.logout, () => _confirmLogout(context, ref)),
    ];

    return Card(
      child: Column(
        children: items.map((item) {
          return ListTile(
            leading: Icon(item.$2, color: AppColors.primary),
            title: Text(item.$1),
            trailing: const Icon(Icons.chevron_right),
            onTap: item.$3,
          );
        }).toList(),
      ),
    );
  }

  void _showKindleSettings(BuildContext context, bool isDesktop) {
    showModalBottomSheet(
      context: context,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(
          top: isDesktop
              ? const Radius.circular(16)
              : Radius.circular(16.r),
        ),
      ),
      builder: (context) => SafeArea(
        child: Padding(
          padding: isDesktop
              ? const EdgeInsets.all(24)
              : EdgeInsets.all(16.w),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Kindle 设置',
                style: TextStyle(
                  fontSize: isDesktop ? 20 : 18.sp,
                  fontWeight: FontWeight.bold,
                ),
              ),
              SizedBox(height: isDesktop ? 20 : 16.h),
              ListTile(
                leading: const Icon(Icons.email, color: AppColors.primary),
                title: const Text('Kindle 邮箱'),
                subtitle: const Text('设置接收推送的 Kindle 邮箱地址'),
                trailing: const Icon(Icons.chevron_right),
                onTap: () {
                  Navigator.pop(context);
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Kindle 邮箱设置功能开发中')),
                  );
                },
              ),
              ListTile(
                leading: const Icon(Icons.sync, color: AppColors.primary),
                title: const Text('自动同步'),
                subtitle: const Text('加入书架后自动推送到 Kindle'),
                trailing: Switch(value: false, onChanged: (v) {}),
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _showHelpFeedback(BuildContext context, bool isDesktop) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('帮助与反馈'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('常见问题：'),
            SizedBox(height: isDesktop ? 10 : 8.h),
            const Text('1. 如何推送图书到 Kindle？'),
            const Text('   在图书详情页点击"加入书架"，系统会自动推送到您设置的 Kindle 邮箱。'),
            SizedBox(height: isDesktop ? 10 : 8.h),
            const Text('2. 支持哪些电子书格式？'),
            const Text('   目前支持 EPUB、MOBI、PDF 格式。'),
            SizedBox(height: isDesktop ? 10 : 8.h),
            const Text('3. 如何联系客服？'),
            const Text('   请发送邮件至 support@ikindle.local'),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('关闭'),
          ),
        ],
      ),
    );
  }

  void _showAbout(BuildContext context, bool isDesktop) {
    showAboutDialog(
      context: context,
      applicationName: 'iKindle',
      applicationVersion: '1.0.0',
      applicationIcon: Container(
        width: isDesktop ? 52 : 48.w,
        height: isDesktop ? 52 : 48.w,
        decoration: BoxDecoration(
          color: AppColors.primary,
          borderRadius: isDesktop
              ? const BorderRadius.all(Radius.circular(10))
              : BorderRadius.circular(8.r),
        ),
        child: const Icon(Icons.book, color: Colors.white),
      ),
      applicationLegalese: '© 2026 iKindle. All rights reserved.',
      children: [
        SizedBox(height: isDesktop ? 20 : 16.h),
        const Text('iKindle 是一款支持多端阅读和 Kindle 推送的电子书平台。'),
      ],
    );
  }

  void _confirmLogout(BuildContext context, WidgetRef ref) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('确认退出'),
        content: const Text('确定要退出登录吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () {
              ref.read(authStateProvider.notifier).logout();
              Navigator.pop(context);
            },
            child: const Text('退出', style: TextStyle(color: AppColors.error)),
          ),
        ],
      ),
    );
  }
}
