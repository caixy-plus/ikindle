import 'package:dio/dio.dart';
import '../../data/models/api_response.dart';
import '../../data/models/book.dart';
import '../../data/models/bookshelf_item.dart';
import '../../data/models/category.dart';
import '../../data/models/login_request.dart';
import '../../data/models/login_response.dart';
import '../../data/models/order.dart';
import '../../data/models/page_result.dart';
import '../../data/models/tag.dart';
import '../../data/models/user.dart';
import 'dio_client.dart';

class ApiClient {
  final Dio _dio;

  ApiClient([Dio? dio]) : _dio = dio ?? DioClient.instance;

  // Auth
  Future<ApiResponse<User>> register(Map<String, dynamic> body) async {
    final resp = await _dio.post('/users/register', data: body);
    return _parse(resp.data, (json) => User.fromJson(json as Map<String, dynamic>));
  }

  Future<ApiResponse<LoginResponse>> login(LoginRequest request) async {
    final resp = await _dio.post('/users/login', data: request.toJson());
    return _parse(resp.data, (json) => LoginResponse.fromJson(json as Map<String, dynamic>));
  }

  Future<ApiResponse<User>> getCurrentUser() async {
    final resp = await _dio.get('/users/me');
    return _parse(resp.data, (json) => User.fromJson(json as Map<String, dynamic>));
  }

  // OAuth
  Future<ApiResponse<LoginResponse>> oauthExchange(String code) async {
    final resp = await _dio.post('/oauth/exchange', data: {'code': code});
    return _parse(resp.data, (json) => LoginResponse.fromJson(json as Map<String, dynamic>));
  }

  // Books
  Future<ApiResponse<PageResult<Book>>> getBooks({
    int page = 0,
    int size = 20,
    int? categoryId,
    String? keyword,
  }) async {
    final resp = await _dio.get('/books', queryParameters: {
      'page': page,
      'size': size,
      if (categoryId != null) 'categoryId': categoryId,
      if (keyword != null) 'keyword': keyword,
    });
    return _parse(
      resp.data,
      (json) => PageResult<Book>.fromJson(
        json as Map<String, dynamic>,
        (item) => Book.fromJson(item as Map<String, dynamic>),
      ),
    );
  }

  Future<ApiResponse<Book>> getBookById(int id) async {
    final resp = await _dio.get('/books/$id');
    return _parse(resp.data, (json) => Book.fromJson(json as Map<String, dynamic>));
  }

  // Categories
  Future<ApiResponse<List<Category>>> getCategories() async {
    final resp = await _dio.get('/categories');
    return _parse(
      resp.data,
      (json) => (json as List).map((e) => Category.fromJson(e as Map<String, dynamic>)).toList(),
    );
  }

  // Tags
  Future<ApiResponse<List<Tag>>> getTags() async {
    final resp = await _dio.get('/tags');
    return _parse(
      resp.data,
      (json) => (json as List).map((e) => Tag.fromJson(e as Map<String, dynamic>)).toList(),
    );
  }

  // Orders
  Future<ApiResponse<PageResult<Order>>> getOrders({
    int page = 0,
    int size = 20,
  }) async {
    final resp = await _dio.get('/orders', queryParameters: {'page': page, 'size': size});
    return _parse(
      resp.data,
      (json) => PageResult<Order>.fromJson(
        json as Map<String, dynamic>,
        (item) => Order.fromJson(item as Map<String, dynamic>),
      ),
    );
  }

  Future<ApiResponse<Order>> createOrder(Map<String, dynamic> body) async {
    final resp = await _dio.post('/orders', data: body);
    return _parse(resp.data, (json) => Order.fromJson(json as Map<String, dynamic>));
  }

  Future<ApiResponse<Order>> payOrder(int id, Map<String, dynamic> body) async {
    final resp = await _dio.post('/orders/$id/pay', data: body);
    return _parse(resp.data, (json) => Order.fromJson(json as Map<String, dynamic>));
  }

  // Bookshelf — 对齐后端 Controller 路径
  Future<ApiResponse<PageResult<BookshelfItem>>> getBookshelf({
    required int userId,
    int page = 0,
    int size = 50,
    String? syncStatus,
  }) async {
    final resp = await _dio.get('/bookshelf/user/$userId', queryParameters: {
      'page': page,
      'size': size,
      if (syncStatus != null) 'syncStatus': syncStatus,
    });
    return _parse(
      resp.data,
      (json) => PageResult<BookshelfItem>.fromJson(
        json as Map<String, dynamic>,
        (item) => BookshelfItem.fromJson(item as Map<String, dynamic>),
      ),
    );
  }

  Future<ApiResponse<BookshelfItem>> addToBookshelf({
    required int userId,
    required int bookId,
  }) async {
    final resp = await _dio.post('/bookshelf/add', queryParameters: {
      'userId': userId,
      'bookId': bookId,
    });
    return _parse(resp.data, (json) => BookshelfItem.fromJson(json as Map<String, dynamic>));
  }

  Future<ApiResponse<BookshelfItem>> updateProgress(int id, Map<String, dynamic> body) async {
    final resp = await _dio.put('/bookshelf/$id/progress', data: body);
    return _parse(resp.data, (json) => BookshelfItem.fromJson(json as Map<String, dynamic>));
  }

  Future<ApiResponse<BookshelfItem>> toggleFavorite(int id) async {
    final resp = await _dio.put('/bookshelf/$id/favorite');
    return _parse(resp.data, (json) => BookshelfItem.fromJson(json as Map<String, dynamic>));
  }

  ApiResponse<T> _parse<T>(dynamic data, T Function(Object?) fromJson) {
    final map = data as Map<String, dynamic>;
    return ApiResponse<T>.fromJson(map, (json) => fromJson(json));
  }
}
