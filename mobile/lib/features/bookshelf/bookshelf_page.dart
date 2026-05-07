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
}

class _BookshelfList extends ConsumerWidget {
  final String? syncStatus;
  const _BookshelfList({this.syncStatus});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncValue = ref.watch(bookshelfProvider(syncStatus));

    return asyncValue.when(
      data: (result) {
        if (result.items.isEmpty) {
          return const Center(child: Text('暂无图书'));
        }
        return ListView.builder(
          padding: EdgeInsets.all(16.w),
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
    return Card(
      margin: EdgeInsets.only(bottom: 12.h),
      child: InkWell(
        onTap: () => context.push('/book/${item.bookId}'),
        borderRadius: BorderRadius.circular(12.r),
        child: Padding(
          padding: EdgeInsets.all(12.w),
          child: Row(
            children: [
              Container(
                width: 70.w,
                height: 95.h,
                decoration: BoxDecoration(
                  color: AppColors.primaryLight,
                  borderRadius: BorderRadius.circular(8.r),
                ),
                child: const Icon(Icons.book, color: AppColors.primary),
              ),
              SizedBox(width: 12.w),
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
                    SizedBox(height: 4.h),
                    if (item.author != null)
                      Text(
                        item.author!,
                        style: Theme.of(context).textTheme.bodySmall,
                      ),
                    SizedBox(height: 8.h),
                    Row(
                      children: [
                        _StatusChip(status: item.syncStatus),
                        const Spacer(),
                        if (item.progress != null)
                          Text(
                            '${(item.progress! * 100).toInt()}%',
                            style: TextStyle(
                              fontSize: 12.sp,
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
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 8.w, vertical: 2.h),
      decoration: BoxDecoration(
        color: _color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(4.r),
      ),
      child: Text(
        _label,
        style: TextStyle(fontSize: 11.sp, color: _color),
      ),
    );
  }
}
