import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:go_router/go_router.dart';
import '../../core/api/api_client.dart';
import '../../core/providers/api_provider.dart';
import '../../core/theme/app_colors.dart';
import '../../data/models/book.dart';
import '../../data/models/category.dart';

final categoryListProvider = FutureProvider<List<Category>>((ref) async {
  final api = ref.read(apiClientProvider);
  final resp = await api.getCategories();
  return resp.data ?? [];
});

final booksByCategoryProvider =
    FutureProvider.family<List<Book>, int>((ref, categoryId) async {
  final api = ref.read(apiClientProvider);
  final resp = await api.getBooks(categoryId: categoryId, page: 0, size: 50);
  return resp.data?.items ?? [];
});

class CategoryPage extends ConsumerStatefulWidget {
  const CategoryPage({super.key});

  @override
  ConsumerState<CategoryPage> createState() => _CategoryPageState();
}

class _CategoryPageState extends ConsumerState<CategoryPage> {
  int _selectedCategoryId = 0;

  @override
  Widget build(BuildContext context) {
    final categoriesAsync = ref.watch(categoryListProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('分类')),
      body: categoriesAsync.when(
        data: (categories) {
          if (categories.isEmpty) return const Center(child: Text('暂无分类'));
          if (_selectedCategoryId == 0) {
            _selectedCategoryId = categories.first.id;
          }
          return Row(
            children: [
              Container(
                width: 100.w,
                color: AppColors.background,
                child: ListView.builder(
                  itemCount: categories.length,
                  itemBuilder: (context, index) {
                    final cat = categories[index];
                    final isSelected = cat.id == _selectedCategoryId;
                    return InkWell(
                      onTap: () =>
                          setState(() => _selectedCategoryId = cat.id),
                      child: Container(
                        padding: EdgeInsets.symmetric(
                            vertical: 16.h, horizontal: 12.w),
                        color: isSelected
                            ? AppColors.surface
                            : Colors.transparent,
                        child: Text(
                          cat.name,
                          style: TextStyle(
                            fontSize: 14.sp,
                            fontWeight: isSelected
                                ? FontWeight.bold
                                : FontWeight.normal,
                            color: isSelected
                                ? AppColors.primary
                                : AppColors.textSecondary,
                          ),
                        ),
                      ),
                    );
                  },
                ),
              ),
              Expanded(
                child: _BookGrid(categoryId: _selectedCategoryId),
              ),
            ],
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('加载失败: $e')),
      ),
    );
  }
}

class _BookGrid extends ConsumerWidget {
  final int categoryId;
  const _BookGrid({required this.categoryId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final booksAsync = ref.watch(booksByCategoryProvider(categoryId));

    return booksAsync.when(
      data: (books) {
        if (books.isEmpty) return const Center(child: Text('该分类暂无图书'));
        return GridView.builder(
          padding: EdgeInsets.all(12.w),
          gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2,
            childAspectRatio: 0.65,
            crossAxisSpacing: 12.w,
            mainAxisSpacing: 12.h,
          ),
          itemCount: books.length,
          itemBuilder: (context, index) {
            final book = books[index];
            return _CategoryBookCard(book: book);
          },
        );
      },
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (e, _) => Center(child: Text('加载失败: $e')),
    );
  }
}

class _CategoryBookCard extends StatelessWidget {
  final Book book;
  const _CategoryBookCard({required this.book});

  @override
  Widget build(BuildContext context) {
    return Card(
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: () => context.push('/book/${book.id}'),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Expanded(
              child: Container(
                width: double.infinity,
                color: AppColors.primaryLight,
                child: const Icon(Icons.book, color: AppColors.primary),
              ),
            ),
            Padding(
              padding: EdgeInsets.all(8.w),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    book.title,
                    style: Theme.of(context).textTheme.titleSmall,
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                  ),
                  SizedBox(height: 4.h),
                  Text(
                    book.author,
                    style: Theme.of(context).textTheme.bodySmall,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                  SizedBox(height: 4.h),
                  if (book.price != null)
                    Text(
                      '¥${book.price}',
                      style: TextStyle(
                        fontSize: 14.sp,
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
    );
  }
}
