import 'package:json_annotation/json_annotation.dart';

part 'tag.g.dart';

@JsonSerializable()
class Tag {
  final int id;
  final String name;
  final String code;
  final String? description;
  final String? color;
  final bool? enabled;
  final int? usageCount;

  Tag({
    required this.id,
    required this.name,
    required this.code,
    this.description,
    this.color,
    this.enabled,
    this.usageCount,
  });

  factory Tag.fromJson(Map<String, dynamic> json) => _$TagFromJson(json);
  Map<String, dynamic> toJson() => _$TagToJson(this);
}
