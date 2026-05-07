// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'order.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Order _$OrderFromJson(Map<String, dynamic> json) => Order(
  id: (json['id'] as num).toInt(),
  userId: (json['userId'] as num).toInt(),
  orderNo: json['orderNo'] as String,
  totalAmount: (json['totalAmount'] as num).toDouble(),
  status: json['status'] as String,
  paymentMethod: json['paymentMethod'] as String?,
  paymentStatus: json['paymentStatus'] as String?,
  createdTime: json['createdTime'] as String?,
  updatedTime: json['updatedTime'] as String?,
  items: (json['items'] as List<dynamic>?)
      ?.map((e) => OrderItem.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$OrderToJson(Order instance) => <String, dynamic>{
  'id': instance.id,
  'userId': instance.userId,
  'orderNo': instance.orderNo,
  'totalAmount': instance.totalAmount,
  'status': instance.status,
  'paymentMethod': instance.paymentMethod,
  'paymentStatus': instance.paymentStatus,
  'createdTime': instance.createdTime,
  'updatedTime': instance.updatedTime,
  'items': instance.items,
};

OrderItem _$OrderItemFromJson(Map<String, dynamic> json) => OrderItem(
  id: (json['id'] as num).toInt(),
  bookId: (json['bookId'] as num).toInt(),
  bookTitle: json['bookTitle'] as String,
  price: (json['price'] as num).toDouble(),
  quantity: (json['quantity'] as num).toInt(),
  coverUrl: json['coverUrl'] as String?,
);

Map<String, dynamic> _$OrderItemToJson(OrderItem instance) => <String, dynamic>{
  'id': instance.id,
  'bookId': instance.bookId,
  'bookTitle': instance.bookTitle,
  'price': instance.price,
  'quantity': instance.quantity,
  'coverUrl': instance.coverUrl,
};
