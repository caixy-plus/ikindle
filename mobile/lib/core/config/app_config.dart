class AppConfig {
  static const String baseUrl = String.fromEnvironment(
    'BASE_URL',
    defaultValue: 'http://localhost:18080',
  );

  static const String apiBaseUrl = '$baseUrl/api';

  static const String platformBaseUrl = 'https://app.local.caixy.xin';

  static const int connectTimeout = 10000;
  static const int receiveTimeout = 30000;

  static const String kOAuthClientId = 'a91c9e88f4b842eca985';
  static const String kOAuthScope = 'read';
  static const String kRedirectUri = 'ikindle://oauth/callback';
}
