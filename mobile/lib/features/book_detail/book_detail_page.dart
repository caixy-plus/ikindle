import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import '../../core/api/api_client.dart';
import '../../core/providers/api_provider.dart';
import '../../core/providers/auth_provider.dart';
import '../../core/theme/app_colors.dart';
import '../../data/models/book.dart';

final bookDetailProvider = FutureProvider.family<Book, int>((ref, id) async {
  final api = ref.read(apiClientProvider);
  final resp = await api.getBookById(id);
  if (!resp.isSuccess || resp.data == null) throw Exception(resp.message);
  return resp.data!;
});

class BookDetailPage extends ConsumerWidget {
  final int bookId;
  const BookDetailPage({super.key, required this.bookId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncBook = ref.watch(bookDetailProvider(bookId));

    return Scaffold(
      appBar: AppBar(title: const Text('图书详情')),
      body: asyncBook.when(
        data: (book) => _BookDetailBody(book: book),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('加载失败: $e')),
      ),
      bottomNavigationBar: SafeArea(
        child: Padding(
          padding: EdgeInsets.all(16.w),
          child: asyncBook.when(
            data: (book) => _AddToShelfButton(bookId: bookId),
            loading: () => const ElevatedButton(
              onPressed: null,
              child: Text('加入书架'),
            ),
            error: (_, __) => const SizedBox.shrink(),
          ),
        ),
      ),
    );
  }
}

class _AddToShelfButton extends ConsumerStatefulWidget {
  final int bookId;
  const _AddToShelfButton({required this.bookId});

  @override
  ConsumerState<_AddToShelfButton> createState() => _AddToShelfButtonState();
}

class _AddToShelfButtonState extends ConsumerState<_AddToShelfButton> {
  bool _adding = false;

  Future<void> _addToShelf() async {
    final user = ref.read(authStateProvider).valueOrNull;
    if (user == null) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('请先登录')),
        );
      }
      return;
    }
    setState(() => _adding = true);
    try {
      final api = ref.read(apiClientProvider);
      final resp = await api.addToBookshelf(userId: user.id, bookId: widget.bookId);
      if (resp.isSuccess) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('已成功加入书架')),
          );
        }
      } else {
        throw Exception(resp.message);
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('加入书架失败: $e')),
        );
      }
    } finally {
      if (mounted) setState(() => _adding = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: _adding ? null : _addToShelf,
      child: _adding
          ? const SizedBox(
              height: 20,
              width: 20,
              child: CircularProgressIndicator(strokeWidth: 2),
            )
          : const Text('加入书架'),
    );
  }
}

class _BookDetailBody extends StatelessWidget {
  final Book book;
  const _BookDetailBody({required this.book});

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: EdgeInsets.all(16.w),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Center(
            child: Container(
              width: 160.w,
              height: 220.h,
              decoration: BoxDecoration(
                color: AppColors.primaryLight,
                borderRadius: BorderRadius.circular(12.r),
              ),
              child: const Icon(Icons.book, size: 64, color: AppColors.primary),
            ),
          ),
          SizedBox(height: 24.h),
          Text(
            book.title,
            style: Theme.of(context).textTheme.headlineMedium,
          ),
          if (book.subtitle != null)
            Text(
              book.subtitle!,
              style: Theme.of(context).textTheme.bodyMedium,
            ),
          SizedBox(height: 12.h),
          Row(
            children: [
              if (book.rating != null)
                Row(
                  children: [
                    const Icon(Icons.star, color: AppColors.warning, size: 18),
                    Text(' ${book.rating}'),
                  ],
                ),
              SizedBox(width: 16.w),
              if (book.price != null)
                Text(
                  '¥${book.price}',
                  style: TextStyle(
                    fontSize: 20.sp,
                    fontWeight: FontWeight.bold,
                    color: AppColors.error,
                  ),
                ),
              if (book.originalPrice != null)
                Padding(
                  padding: EdgeInsets.only(left: 8.w),
                  child: Text(
                    '¥${book.originalPrice}',
                    style: TextStyle(
                      fontSize: 14.sp,
                      color: AppColors.textTertiary,
                      decoration: TextDecoration.lineThrough,
                    ),
                  ),
                ),
            ],
          ),
          SizedBox(height: 16.h),
          _InfoRow(label: '作者', value: book.author),
          SizedBox(height: 8.h),
          if (book.salesCount != null)
            _InfoRow(label: '销量', value: '${book.salesCount}'),
          SizedBox(height: 24.h),
          Text('内容简介', style: Theme.of(context).textTheme.titleMedium),
          SizedBox(height: 8.h),
          Text(
            book.description ?? '暂无简介',
            style: Theme.of(context).textTheme.bodyMedium,
          ),
        ],
      ),
    );
  }
}

class _InfoRow extends StatelessWidget {
  final String label;
  final String value;
  const _InfoRow({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Text(
          '$label: ',
          style: TextStyle(
            fontSize: 14.sp,
            color: AppColors.textSecondary,
          ),
        ),
        Text(
          value,
          style: TextStyle(
            fontSize: 14.sp,
            color: AppColors.textPrimary,
            fontWeight: FontWeight.w500,
          ),
        ),
      ],
    );
  }
}
