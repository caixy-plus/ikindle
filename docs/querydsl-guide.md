# QueryDSL 使用指南

## 概述

本项目使用 QueryDSL 来实现类型安全的查询，避免硬编码 SQL 语句。

## 配置

### Maven 配置

在 `pom.xml` 中已经配置了 QueryDSL 相关依赖：

```xml
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
    <version>5.0.0</version>
    <classifier>jakarta</classifier>
</dependency>
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
    <version>5.0.0</version>
    <scope>provided</scope>
</dependency>
```

### APT 插件配置

```xml
<plugin>
    <groupId>com.mysema.maven</groupId>
    <artifactId>apt-maven-plugin</artifactId>
    <version>1.1.3</version>
    <executions>
        <execution>
            <goals>
                <goal>process</goal>
            </goals>
            <configuration>
                <outputDirectory>target/generated-sources/java</outputDirectory>
                <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## 使用方法

### 1. 生成 Q 类

编译项目时会自动生成 Q 类：

```bash
mvn compile
```

生成的 Q 类位于 `target/generated-sources/java` 目录下。

### 2. 自定义 Repository 接口

创建自定义接口定义复杂查询方法：

```java
public interface BookRepositoryCustom {
    Page<Book> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<Book> searchBooks(String keyword, Pageable pageable);
    // ... 其他复杂查询方法
}
```

### 3. 实现自定义 Repository

```java
@Repository
public class BookRepositoryCustomImpl implements BookRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Autowired
    public BookRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    
    @Override
    public Page<Book> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        QBook book = QBook.book;
        
        JPAQuery<Book> query = queryFactory
                .selectFrom(book)
                .where(book.price.between(minPrice, maxPrice)
                        .and(book.published.isTrue()))
                .orderBy(book.createdTime.desc());
        
        long total = query.fetchCount();
        List<Book> books = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        return new PageImpl<>(books, pageable, total);
    }
}
```

### 4. 继承自定义接口

```java
@Repository
public interface BookRepository extends BaseRepository<Book, Long>, BookRepositoryCustom {
    // 基本的 Spring Data JPA 方法
    Page<Book> findByPublished(Boolean published, Pageable pageable);
}
```

## 查询示例

### 基础查询

```java
// 简单条件查询
QBook book = QBook.book;
List<Book> books = queryFactory
    .selectFrom(book)
    .where(book.published.isTrue())
    .fetch();
```

### 复杂条件查询

```java
// 多条件查询
BooleanBuilder predicate = new BooleanBuilder();
predicate.and(book.published.isTrue());
predicate.and(book.price.goe(minPrice));
predicate.and(book.price.loe(maxPrice));

List<Book> books = queryFactory
    .selectFrom(book)
    .where(predicate)
    .fetch();
```

### 关联查询

```java
// 关联查询
QBook book = QBook.book;
QCategory category = QCategory.category;

List<Book> books = queryFactory
    .selectFrom(book)
    .join(book.category, category)
    .where(category.name.eq("技术"))
    .fetch();
```

### 分页查询

```java
// 分页查询
QBook book = QBook.book;
Pageable pageable = PageRequest.of(0, 10);

JPAQuery<Book> query = queryFactory
    .selectFrom(book)
    .where(book.published.isTrue())
    .orderBy(book.createdTime.desc());

long total = query.fetchCount();
List<Book> books = query
    .offset(pageable.getOffset())
    .limit(pageable.getPageSize())
    .fetch();

return new PageImpl<>(books, pageable, total);
```

## 注意事项

1. **编译顺序**：确保先编译生成 Q 类，再编译使用 Q 类的代码
2. **导入 Q 类**：使用生成的 Q 类时，需要正确导入
3. **类型安全**：QueryDSL 提供编译时类型检查，避免运行时错误
4. **性能优化**：合理使用索引和查询优化

## 当前状态

目前项目中的 QueryDSL 实现是占位符，需要：

1. 编译项目生成 Q 类
2. 完善自定义 Repository 实现
3. 添加更多复杂查询示例

## 下一步

1. 运行 `mvn compile` 生成 Q 类
2. 完善 `BookRepositoryCustomImpl` 和 `CategoryRepositoryCustomImpl` 的实现
3. 添加更多业务相关的复杂查询
4. 编写单元测试验证查询功能 