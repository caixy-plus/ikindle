import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../api/api_client.dart';
import '../api/dio_client.dart';

final apiClientProvider = Provider<ApiClient>((ref) {
  return ApiClient(DioClient.instance);
});
