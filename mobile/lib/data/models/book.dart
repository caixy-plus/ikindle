import 'package:json_annotation/json_annotation.dart';

part 'book.g.dart';

@JsonSerializable()
class Book {
  final int id;
  final String title;
  final String? subtitle;
  final String author;
  final String? description;
  final String? coverUrl;
  final String? fileUrl;
  final double? price;
  final double? originalPrice;
  final double? rating;
  final int? salesCount;
  final int? stockCount;
  final bool? published;
  final String? createdTime;
  final String? updatedTime;

  Book({
    required this.id,
    required this.title,
    this.subtitle,
    required this.author,
    this.description,
    this.coverUrl,
    this.fileUrl,
    this.price,
    this.originalPrice,
    this.rating,
    this.salesCount,
    this.stockCount,
    this.published,
    this.createdTime,
    this.updatedTime,
  });

  factory Book.fromJson(Map<String, dynamic> json) => _$BookFromJson(json);
  Map<String, dynamic> toJson() => _$BookToJson(this);
}
