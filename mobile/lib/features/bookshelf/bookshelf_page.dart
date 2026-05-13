import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:go_router/go_router.dart';
import '../../core/api/api_client.dart';
import '../../core/providers/api_provider.dart';
import '../../core/providers/auth_provider.dart';
import '../../core/theme/app_colors.dart';
import '../../data/models/bookshelf_item.dart';
import '../../data/models/page_result.dart';

final bookshelfProvider =
    FutureProvider.family<PageResult<BookshelfItem>, String?>((ref, status) async {
  final api = ref.read(apiClientProvider);
  final user = ref.read(authStateProvider).valueOrNull;
  if (user == null) {
    return PageResult(items: [], total: 0, page: 0, size: 50, totalPages: 0);
  }
  final resp = await api.getBookshelf(
    userId: user.id,
    page: 0,
    size: 50,
    syncStatus: status,
  );
  if (!resp.isSuccess) throw Exception(resp.message);
  return resp.data ?? PageResult(items: [], total: 0, page: 0, size: 50, totalPages: 0);
});

class BookshelfPage extends ConsumerStatefulWidget {
  const BookshelfPage({super.key});

  @override
  ConsumerState<BookshelfPage> createState() => _BookshelfPageState();
}

class _BookshelfPageState extends ConsumerState<BookshelfPage>
    with SingleTickerProviderStateMixin {
  late final TabController _tabController;

  final _tabs = const [
    ('全部', null),
    ('等待中', 'PENDING'),
    ('已同步', 'COMPLETED'),
    ('待回执', 'RECEIPT_PENDING'),
    ('已完成', 'DONE'),
  ];

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: _tabs.length, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final authState = ref.watch(authStateProvider);
    final isLoggedIn = authState.valueOrNull != null;
    final isDesktop = Platform.isWindows || Platform.isMacOS || Platform.isLinux;

    if (!isLoggedIn) {
      return _buildLoginPrompt(context, isDesktop);
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('我的书架'),
        bottom: TabBar(
          controller: _tabController,
          isScrollable: true,
          tabs: _tabs.map((t) => Tab(text: t.$1)).toList(),
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: _tabs.map((t) => _BookshelfList(syncStatus: t.$2)).toList(),
      ),
    );
  }

  Widget _buildLoginPrompt(BuildContext context, bool isDesktop) {
    return Scaffold(
      appBar: AppBar(title: const Text('我的书架')),
      body: Center(
        child: ConstrainedBox(
          constraints: isDesktop
              ? const BoxConstraints(maxWidth: 400)
              : const BoxConstraints(),
          child: Padding(
            padding: isDesktop
                ? const EdgeInsets.all(32)
                : EdgeInsets.all(24.w),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  Icons.menu_book,
                  size: isDesktop ? 80 : 64.sp,
                  color: AppColors.textTertiary,
                ),
                SizedBox(height: isDesktop ? 24 : 16.h),
                Text(
                  '登录后查看书架',
                  style: TextStyle(
                    fontSize: isDesktop ? 20 : 18.sp,
                    fontWeight: FontWeight.bold,
                    color: AppColors.textPrimary,
                  ),
                ),
                SizedBox(height: isDesktop ? 12 : 8.h),
                Text(
                  '将喜欢的图书加入书架，随时阅读',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: isDesktop ? 14 : 13.sp,
                    color: AppColors.textSecondary,
                  ),
                ),
                SizedBox(height: isDesktop ? 32 : 24.h),
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: () => context.push('/login'),
                    child: const Text('立即登录'),
                  ),
                ),
                SizedBox(height: isDesktop ? 16 : 12.h),
                TextButton(
                  onPressed: () => context.push('/register'),
                  child: const Text('还没有账号？去注册'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _BookshelfList extends ConsumerWidget {
  final String? syncStatus;
  const _BookshelfList({this.syncStatus});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncValue = ref.watch(bookshelfProvider(syncStatus));
    final isDesktop = Platform.isWindows || Platform.isMacOS || Platform.isLinux;

    return asyncValue.when(
      data: (result) {
        if (result.items.isEmpty) {
          return const Center(child: Text('暂无图书'));
        }
        return ListView.builder(
          padding: isDesktop
              ? const EdgeInsets.all(24)
              : EdgeInsets.all(16.w),
          itemCount: result.items.length,
          itemBuilder: (context, index) {
            final item = result.items[index];
            return _BookshelfCard(item: item);
          },
        );
      },
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (e, _) => Center(child: Text('加载失败: $e')),
    );
  }
}

class _BookshelfCard extends StatelessWidget {
  final BookshelfItem item;
  const _BookshelfCard({required this.item});

  @override
  Widget build(BuildContext context) {
    final isDesktop = Platform.isWindows || Platform.isMacOS || Platform.isLinux;

    return Card(
      margin: isDesktop
          ? const EdgeInsets.only(bottom: 16)
          : EdgeInsets.only(bottom: 12.h),
      child: InkWell(
        onTap: () => context.push('/book/${item.bookId}'),
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
                width: isDesktop ? 80 : 70.w,
                height: isDesktop ? 110 : 95.h,
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
                      item.bookTitle,
                      style: Theme.of(context).textTheme.titleMedium,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                    SizedBox(height: isDesktop ? 6 : 4.h),
                    if (item.author != null)
                      Text(
                        item.author!,
                        style: Theme.of(context).textTheme.bodySmall,
                      ),
                    SizedBox(height: isDesktop ? 10 : 8.h),
                    Row(
                      children: [
                        _StatusChip(status: item.syncStatus),
                        const Spacer(),
                        if (item.progress != null)
                          Text(
                            '${(item.progress! * 100).toInt()}%',
                            style: TextStyle(
                              fontSize: isDesktop ? 13 : 12.sp,
                              color: AppColors.textTertiary,
                            ),
                          ),
                      ],
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

class _StatusChip extends StatelessWidget {
  final String status;
  const _StatusChip({required this.status});

  Color get _color {
    switch (status) {
      case 'COMPLETED':
        return AppColors.success;
      case 'PENDING':
        return AppColors.warning;
      case 'FAILED':
        return AppColors.error;
      default:
        return AppColors.textTertiary;
    }
  }

  String get _label {
    switch (status) {
      case 'PENDING':
        return '等待中';
      case 'COMPLETED':
        return '已同步';
      case 'RECEIPT_PENDING':
        return '待回执';
      case 'DONE':
        return '已完成';
      case 'FAILED':
        return '失败';
      default:
        return status;
    }
  }

  @override
  Widget build(BuildContext context) {
    final isDesktop = Platform.isWindows || Platform.isMacOS || Platform.isLinux;

    return Container(
      padding: isDesktop
          ? const EdgeInsets.symmetric(horizontal: 10, vertical: 3)
          : EdgeInsets.symmetric(horizontal: 8.w, vertical: 2.h),
      decoration: BoxDecoration(
        color: _color.withOpacity(0.1),
        borderRadius: isDesktop
            ? const BorderRadius.all(Radius.circular(4))
            : BorderRadius.circular(4.r),
      ),
      child: Text(
        _label,
        style: TextStyle(
          fontSize: isDesktop ? 12 : 11.sp,
          color: _color,
        ),
      ),
    );
  }
}
