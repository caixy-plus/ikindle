import 'package:json_annotation/json_annotation.dart';

part 'category.g.dart';

@JsonSerializable()
class Category {
  final int id;
  final String name;
  final String code;
  final String? description;
  final int? sortOrder;
  final bool? enabled;
  final int? parentId;
  final List<Category>? children;

  Category({
    required this.id,
    required this.name,
    required this.code,
    this.description,
    this.sortOrder,
    this.enabled,
    this.parentId,
    this.children,
  });

  factory Category.fromJson(Map<String, dynamic> json) =>
      _$CategoryFromJson(json);
  Map<String, dynamic> toJson() => _$CategoryToJson(this);
}
