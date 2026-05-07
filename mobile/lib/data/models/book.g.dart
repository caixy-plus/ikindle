// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'book.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Book _$BookFromJson(Map<String, dynamic> json) => Book(
  id: (json['id'] as num).toInt(),
  title: json['title'] as String,
  subtitle: json['subtitle'] as String?,
  author: json['author'] as String,
  description: json['description'] as String?,
  coverUrl: json['coverUrl'] as String?,
  fileUrl: json['fileUrl'] as String?,
  price: (json['price'] as num?)?.toDouble(),
  originalPrice: (json['originalPrice'] as num?)?.toDouble(),
  rating: (json['rating'] as num?)?.toDouble(),
  salesCount: (json['salesCount'] as num?)?.toInt(),
  stockCount: (json['stockCount'] as num?)?.toInt(),
  published: json['published'] as bool?,
  createdTime: json['createdTime'] as String?,
  updatedTime: json['updatedTime'] as String?,
);

Map<String, dynamic> _$BookToJson(Book instance) => <String, dynamic>{
  'id': instance.id,
  'title': instance.title,
  'subtitle': instance.subtitle,
  'author': instance.author,
  'description': instance.description,
  'coverUrl': instance.coverUrl,
  'fileUrl': instance.fileUrl,
  'price': instance.price,
  'originalPrice': instance.originalPrice,
  'rating': instance.rating,
  'salesCount': instance.salesCount,
  'stockCount': instance.stockCount,
  'published': instance.published,
  'createdTime': instance.createdTime,
  'updatedTime': instance.updatedTime,
};
