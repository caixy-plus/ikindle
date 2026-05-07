import 'package:json_annotation/json_annotation.dart';

part 'bookshelf_item.g.dart';

@JsonSerializable()
class BookshelfItem {
  final int id;
  final int userId;
  final int bookId;
  final String bookTitle;
  final String? bookCoverUrl;
  final String? author;
  final double? progress;
  final int? currentPage;
  final int? totalPages;
  final String? lastReadPosition;
  final bool favorite;
  final String syncStatus;
  final String? createdTime;
  final String? updatedTime;

  BookshelfItem({
    required this.id,
    required this.userId,
    required this.bookId,
    required this.bookTitle,
    this.bookCoverUrl,
    this.author,
    this.progress,
    this.currentPage,
    this.totalPages,
    this.lastReadPosition,
    this.favorite = false,
    this.syncStatus = 'PENDING',
    this.createdTime,
    this.updatedTime,
  });

  factory BookshelfItem.fromJson(Map<String, dynamic> json) =>
      _$BookshelfItemFromJson(json);
  Map<String, dynamic> toJson() => _$BookshelfItemToJson(this);
}
