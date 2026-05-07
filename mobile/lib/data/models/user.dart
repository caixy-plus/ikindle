import 'package:json_annotation/json_annotation.dart';

part 'user.g.dart';

@JsonSerializable()
class User {
  final int id;
  final String username;
  final String? nickname;
  final String? avatarUrl;
  final String? signature;
  final String? phone;
  final bool phoneVerified;
  final String? email;
  final bool emailVerified;
  final bool enabled;
  final String? createdTime;
  final String? updatedTime;

  User({
    required this.id,
    required this.username,
    this.nickname,
    this.avatarUrl,
    this.signature,
    this.phone,
    this.phoneVerified = false,
    this.email,
    this.emailVerified = false,
    this.enabled = true,
    this.createdTime,
    this.updatedTime,
  });

  factory User.fromJson(Map<String, dynamic> json) => _$UserFromJson(json);
  Map<String, dynamic> toJson() => _$UserToJson(this);
}
