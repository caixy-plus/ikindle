// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

User _$UserFromJson(Map<String, dynamic> json) => User(
  id: (json['id'] as num).toInt(),
  username: json['username'] as String,
  nickname: json['nickname'] as String?,
  avatarUrl: json['avatarUrl'] as String?,
  signature: json['signature'] as String?,
  phone: json['phone'] as String?,
  phoneVerified: json['phoneVerified'] as bool? ?? false,
  email: json['email'] as String?,
  emailVerified: json['emailVerified'] as bool? ?? false,
  enabled: json['enabled'] as bool? ?? true,
  createdTime: json['createdTime'] as String?,
  updatedTime: json['updatedTime'] as String?,
);

Map<String, dynamic> _$UserToJson(User instance) => <String, dynamic>{
  'id': instance.id,
  'username': instance.username,
  'nickname': instance.nickname,
  'avatarUrl': instance.avatarUrl,
  'signature': instance.signature,
  'phone': instance.phone,
  'phoneVerified': instance.phoneVerified,
  'email': instance.email,
  'emailVerified': instance.emailVerified,
  'enabled': instance.enabled,
  'createdTime': instance.createdTime,
  'updatedTime': instance.updatedTime,
};
