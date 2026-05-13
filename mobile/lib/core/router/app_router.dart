import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../features/auth/login_page.dart';
import '../../features/auth/oauth_login_page.dart';
import '../../features/auth/register_page.dart';
import '../../features/home/home_page.dart';
import '../../features/bookshelf/bookshelf_page.dart';
import '../../features/book_detail/book_detail_page.dart';
import '../../features/category/category_page.dart';
import '../../features/profile/profile_page.dart';
import '../../features/order/order_page.dart';
import '../../features/recharge/recharge_page.dart';
import '../providers/auth_provider.dart';

// 需要登录才能访问的页面
final _authRequiredRoutes = [
  '/bookshelf',
  '/orders',
  '/recharge',
  '/profile',
];

final routerProvider = Provider<GoRouter>((ref) {
  final authState = ref.watch(authStateProvider);

  return GoRouter(
    initialLocation: '/',
    redirect: (context, state) {
      final isLoggedIn = authState.valueOrNull != null;
      final location = state.matchedLocation;
      
      // 认证相关页面
      final isAuthRoute = location == '/login' ||
          location == '/register' ||
          location == '/oauth-login';

      // 检查当前页面是否需要登录
      final requiresAuth = _authRequiredRoutes.any((route) => 
        location == route || location.startsWith('$route/'));

      // 未登录且访问需要登录的页面 -> 跳转到登录
      if (!isLoggedIn && requiresAuth) {
        return '/login';
      }
      
      // 已登录且访问登录页 -> 跳转到首页
      if (isLoggedIn && isAuthRoute) {
        return '/';
      }
      
      return null;
    },
    routes: [
      GoRoute(
        path: '/',
        builder: (context, state) => const HomePage(),
      ),
      GoRoute(
        path: '/login',
        builder: (context, state) => const LoginPage(),
      ),
      GoRoute(
        path: '/oauth-login',
        builder: (context, state) => const OAuthLoginPage(),
      ),
      GoRoute(
        path: '/register',
        builder: (context, state) => const RegisterPage(),
      ),
      GoRoute(
        path: '/bookshelf',
        builder: (context, state) => const BookshelfPage(),
      ),
      GoRoute(
        path: '/book/:id',
        builder: (context, state) {
          final id = int.parse(state.pathParameters['id']!);
          return BookDetailPage(bookId: id);
        },
      ),
      GoRoute(
        path: '/category',
        builder: (context, state) => const CategoryPage(),
      ),
      GoRoute(
        path: '/profile',
        builder: (context, state) => const ProfilePage(),
      ),
      GoRoute(
        path: '/orders',
        builder: (context, state) => const OrderPage(),
      ),
      GoRoute(
        path: '/recharge',
        builder: (context, state) => const RechargePage(),
      ),
    ],
  );
});
