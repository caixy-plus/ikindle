import 'package:json_annotation/json_annotation.dart';

part 'order.g.dart';

@JsonSerializable()
class Order {
  final int id;
  final int userId;
  final String orderNo;
  final double totalAmount;
  final String status;
  final String? paymentMethod;
  final String? paymentStatus;
  final String? createdTime;
  final String? updatedTime;
  final List<OrderItem>? items;

  Order({
    required this.id,
    required this.userId,
    required this.orderNo,
    required this.totalAmount,
    required this.status,
    this.paymentMethod,
    this.paymentStatus,
    this.createdTime,
    this.updatedTime,
    this.items,
  });

  factory Order.fromJson(Map<String, dynamic> json) => _$OrderFromJson(json);
  Map<String, dynamic> toJson() => _$OrderToJson(this);
}

@JsonSerializable()
class OrderItem {
  final int id;
  final int bookId;
  final String bookTitle;
  final double price;
  final int quantity;
  final String? coverUrl;

  OrderItem({
    required this.id,
    required this.bookId,
    required this.bookTitle,
    required this.price,
    required this.quantity,
    this.coverUrl,
  });

  factory OrderItem.fromJson(Map<String, dynamic> json) =>
      _$OrderItemFromJson(json);
  Map<String, dynamic> toJson() => _$OrderItemToJson(this);
}
