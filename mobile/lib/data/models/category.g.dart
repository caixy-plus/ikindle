// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'category.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Category _$CategoryFromJson(Map<String, dynamic> json) => Category(
  id: (json['id'] as num).toInt(),
  name: json['name'] as String,
  code: json['code'] as String,
  description: json['description'] as String?,
  sortOrder: (json['sortOrder'] as num?)?.toInt(),
  enabled: json['enabled'] as bool?,
  parentId: (json['parentId'] as num?)?.toInt(),
  children: (json['children'] as List<dynamic>?)
      ?.map((e) => Category.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$CategoryToJson(Category instance) => <String, dynamic>{
  'id': instance.id,
  'name': instance.name,
  'code': instance.code,
  'description': instance.description,
  'sortOrder': instance.sortOrder,
  'enabled': instance.enabled,
  'parentId': instance.parentId,
  'children': instance.children,
};
