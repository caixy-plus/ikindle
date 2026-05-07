// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bookshelf_item.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

BookshelfItem _$BookshelfItemFromJson(Map<String, dynamic> json) =>
    BookshelfItem(
      id: (json['id'] as num).toInt(),
      userId: (json['userId'] as num).toInt(),
      bookId: (json['bookId'] as num).toInt(),
      bookTitle: json['bookTitle'] as String,
      bookCoverUrl: json['bookCoverUrl'] as String?,
      author: json['author'] as String?,
      progress: (json['progress'] as num?)?.toDouble(),
      currentPage: (json['currentPage'] as num?)?.toInt(),
      totalPages: (json['totalPages'] as num?)?.toInt(),
      lastReadPosition: json['lastReadPosition'] as String?,
      favorite: json['favorite'] as bool? ?? false,
      syncStatus: json['syncStatus'] as String? ?? 'PENDING',
      createdTime: json['createdTime'] as String?,
      updatedTime: json['updatedTime'] as String?,
    );

Map<String, dynamic> _$BookshelfItemToJson(BookshelfItem instance) =>
    <String, dynamic>{
      'id': instance.id,
      'userId': instance.userId,
      'bookId': instance.bookId,
      'bookTitle': instance.bookTitle,
      'bookCoverUrl': instance.bookCoverUrl,
      'author': instance.author,
      'progress': instance.progress,
      'currentPage': instance.currentPage,
      'totalPages': instance.totalPages,
      'lastReadPosition': instance.lastReadPosition,
      'favorite': instance.favorite,
      'syncStatus': instance.syncStatus,
      'createdTime': instance.createdTime,
      'updatedTime': instance.updatedTime,
    };
