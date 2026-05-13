import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:go_router/go_router.dart';
import '../../core/api/api_client.dart';
import '../../core/providers/api_provider.dart';
import '../../core/providers/auth_provider.dart';
import '../../core/theme/app_colors.dart';
import '../../data/models/book.dart';
import '../../data/models/category.dart';
import '../bookshelf/bookshelf_page.dart';
import '../profile/profile_page.dart';

final homeBooksProvider = FutureProvider<List<Book>>((ref) async {
  final api = ref.read(apiClientProvider);
  final resp = await api.getBooks(page: 0, size: 10);
  return resp.data?.items ?? [];
});

final homeCategoriesProvider = FutureProvider<List<Category>>((ref) async {
  final api = ref.read(apiClientProvider);
  final resp = await api.getCategories();
  return resp.data ?? [];
});

class HomePage extends ConsumerStatefulWidget {
  const HomePage({super.key});

  @override
  ConsumerState<HomePage> createState() => _HomePageState();
}

class _HomePageState extends ConsumerState<HomePage> {
  int _currentIndex = 0;

  final _pages = const [
    _HomeBody(),
    BookshelfPage(),
    ProfilePage(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _pages[_currentIndex],
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentIndex,
        onTap: (i) => setState(() => _currentIndex = i),
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home), label: '首页'),
          BottomNavigationBarItem(icon: Icon(Icons.menu_book), label: '书架'),
          BottomNavigationBarItem(icon: Icon(Icons.person), label: '我的'),
        ],
      ),
    );
  }
}

class _HomeBody extends ConsumerWidget {
  const _HomeBody();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final booksAsync = ref.watch(homeBooksProvider);
    final categoriesAsync = ref.watch(homeCategoriesProvider);
    final authState = ref.watch(authStateProvider);
    final isLoggedIn = authState.valueOrNull != null;
    final isDesktop = Platform.isWindows || Platform.isMacOS || Platform.isLinux;

    return SafeArea(
      child: CustomScrollView(
        slivers: [
          SliverToBoxAdapter(
            child: Padding(
              padding: isDesktop
                  ? const EdgeInsets.all(24)
                  : EdgeInsets.all(16.w),
              child: Column(
                children: [
                  TextField(
                    readOnly: true,
                    onTap: () => context.push('/category'),
                    decoration: InputDecoration(
                      hintText: '搜索书名、作者',
                      prefixIcon: const Icon(Icons.search),
                      filled: true,
                      fillColor: AppColors.background,
                      border: OutlineInputBorder(
                        borderRadius: isDesktop
                            ? const BorderRadius.all(Radius.circular(24))
                            : BorderRadius.circular(24.r),
                        borderSide: BorderSide.none,
                      ),
                    ),
                  ),
                  if (!isLoggedIn) ...[
                    SizedBox(height: isDesktop ? 16 : 12.h),
                    _buildGuestBanner(context, isDesktop),
                  ],
                ],
              ),
            ),
          ),
          SliverToBoxAdapter(
            child: Container(
              margin: isDesktop
                  ? const EdgeInsets.symmetric(horizontal: 24)
                  : EdgeInsets.symmetric(horizontal: 16.w),
              height: isDesktop ? 160 : 140.h,
              decoration: BoxDecoration(
                gradient: const LinearGradient(
                  colors: [AppColors.primary, AppColors.primaryDark],
                ),
                borderRadius: isDesktop
                    ? const BorderRadius.all(Radius.circular(16))
                    : BorderRadius.circular(12.r),
              ),
              child: Center(
                child: Text(
                  '知识无界，阅读不停',
                  style: TextStyle(
                    fontSize: isDesktop ? 24 : 20.sp,
                    fontWeight: FontWeight.bold,
                    color: Colors.white,
                  ),
                ),
              ),
            ),
          ),
          SliverToBoxAdapter(
            child: Padding(
              padding: isDesktop
                  ? const EdgeInsets.fromLTRB(24, 32, 24, 12)
                  : EdgeInsets.fromLTRB(16.w, 24.h, 16.w, 8.h),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text('推荐图书', style: Theme.of(context).textTheme.titleMedium),
                  TextButton(
                    onPressed: () => context.push('/category'),
                    child: const Text('更多'),
                  ),
                ],
              ),
            ),
          ),
          booksAsync.when(
            data: (books) => SliverPadding(
              padding: isDesktop
                  ? const EdgeInsets.symmetric(horizontal: 24)
                  : EdgeInsets.symmetric(horizontal: 16.w),
              sliver: SliverList(
                delegate: SliverChildBuilderDelegate(
                  (context, index) => _BookCard(book: books[index]),
                  childCount: books.length,
                ),
              ),
            ),
            loading: () => const SliverToBoxAdapter(
              child: Center(child: CircularProgressIndicator()),
            ),
            error: (e, _) => SliverToBoxAdapter(
              child: Center(child: Text('加载失败: $e')),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildGuestBanner(BuildContext context, bool isDesktop) {
    return Container(
      padding: isDesktop
          ? const EdgeInsets.symmetric(horizontal: 16, vertical: 12)
          : EdgeInsets.symmetric(horizontal: 16.w, vertical: 10.h),
      decoration: BoxDecoration(
        color: AppColors.primary.withOpacity(0.1),
        borderRadius: isDesktop
            ? const BorderRadius.all(Radius.circular(12))
            : BorderRadius.circular(12.r),
        border: Border.all(color: AppColors.primary.withOpacity(0.3)),
      ),
      child: Row(
        children: [
          Icon(Icons.info_outline, color: AppColors.primary, size: isDesktop ? 20 : 18.sp),
          SizedBox(width: isDesktop ? 10 : 8.w),
          Expanded(
            child: Text(
              '您当前以访客身份浏览，登录后可使用书架等功能',
              style: TextStyle(
                fontSize: isDesktop ? 13 : 12.sp,
                color: AppColors.textSecondary,
              ),
            ),
          ),
          TextButton(
            onPressed: () => context.push('/login'),
            child: const Text('去登录'),
          ),
        ],
      ),
    );
  }
}

class _BookCard extends StatelessWidget {
  final Book book;
  const _BookCard({required this.book});

  @override
  Widget build(BuildContext context) {
    final isDesktop = Platform.isWindows || Platform.isMacOS || Platform.isLinux;

    return Card(
      margin: isDesktop
          ? const EdgeInsets.only(bottom: 16)
          : EdgeInsets.only(bottom: 12.h),
      child: InkWell(
        onTap: () => context.push('/book/${book.id}'),
        borderRadius: isDesktop
            ? const BorderRadius.all(Radius.circular(12))
            : BorderRadius.circular(12.r),
        child: Padding(
          padding: isDesktop
              ? const EdgeInsets.all(16)
              : EdgeInsets.all(12.w),
          child: Row(
            children: [
              Container(
                width: isDesktop ? 90 : 80.w,
                height: isDesktop ? 120 : 110.h,
                decoration: BoxDecoration(
                  color: AppColors.primaryLight,
                  borderRadius: isDesktop
                      ? const BorderRadius.all(Radius.circular(8))
                      : BorderRadius.circular(8.r),
                ),
                child: const Icon(Icons.book, color: AppColors.primary),
              ),
              SizedBox(width: isDesktop ? 16 : 12.w),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      book.title,
                      style: Theme.of(context).textTheme.titleMedium,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                    SizedBox(height: isDesktop ? 6 : 4.h),
                    Text(
                      book.author,
                      style: Theme.of(context).textTheme.bodySmall,
                    ),
                    SizedBox(height: isDesktop ? 10 : 8.h),
                    if (book.price != null)
                      Text(
                        '¥${book.price}',
                        style: TextStyle(
                          fontSize: isDesktop ? 18 : 16.sp,
                          fontWeight: FontWeight.bold,
                          color: AppColors.error,
                        ),
                      ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
