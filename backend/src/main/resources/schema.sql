-- iKindle 数据库建表语句
-- -- 生成创建数据库语句
-- CREATE DATABASE iKindle;
-- 用户表
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    avatar_url VARCHAR(500),
    signature VARCHAR(200),
    phone VARCHAR(20),
    phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    email VARCHAR(100),
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

COMMENT ON TABLE users IS '用户表';
COMMENT ON COLUMN users.id IS '主键ID';
COMMENT ON COLUMN users.created_time IS '创建时间';
COMMENT ON COLUMN users.updated_time IS '更新时间';
COMMENT ON COLUMN users.is_deleted IS '是否删除';
COMMENT ON COLUMN users.username IS '用户名';
COMMENT ON COLUMN users.password IS '密码';
COMMENT ON COLUMN users.nickname IS '昵称';
COMMENT ON COLUMN users.avatar_url IS '头像URL';
COMMENT ON COLUMN users.signature IS '个性签名';
COMMENT ON COLUMN users.phone IS '手机号';
COMMENT ON COLUMN users.phone_verified IS '手机号是否验证';
COMMENT ON COLUMN users.email IS '邮箱';
COMMENT ON COLUMN users.email_verified IS '邮箱是否验证';
COMMENT ON COLUMN users.enabled IS '启用状态';

-- 角色表
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
);

COMMENT ON TABLE roles IS '角色表';
COMMENT ON COLUMN roles.id IS '主键ID';
COMMENT ON COLUMN roles.created_time IS '创建时间';
COMMENT ON COLUMN roles.updated_time IS '更新时间';
COMMENT ON COLUMN roles.is_deleted IS '是否删除';
COMMENT ON COLUMN roles.name IS '角色名称';
COMMENT ON COLUMN roles.description IS '角色描述';

-- 权限表
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    type VARCHAR(20) NOT NULL,
    resource VARCHAR(200),
    method VARCHAR(10)
);

COMMENT ON TABLE permissions IS '权限表';
COMMENT ON COLUMN permissions.id IS '主键ID';
COMMENT ON COLUMN permissions.created_time IS '创建时间';
COMMENT ON COLUMN permissions.updated_time IS '更新时间';
COMMENT ON COLUMN permissions.is_deleted IS '是否删除';
COMMENT ON COLUMN permissions.name IS '权限名称';
COMMENT ON COLUMN permissions.description IS '权限描述';
COMMENT ON COLUMN permissions.type IS '权限类型';
COMMENT ON COLUMN permissions.resource IS '权限资源';
COMMENT ON COLUMN permissions.method IS '权限方法';

-- 用户角色关联表
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

COMMENT ON TABLE user_roles IS '用户角色关联表';
COMMENT ON COLUMN user_roles.user_id IS '用户ID';
COMMENT ON COLUMN user_roles.role_id IS '角色ID';

-- 角色权限关联表
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id)
);

COMMENT ON TABLE role_permissions IS '角色权限关联表';
COMMENT ON COLUMN role_permissions.role_id IS '角色ID';
COMMENT ON COLUMN role_permissions.permission_id IS '权限ID';

-- 图书分类表
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(200),
    icon VARCHAR(100),
    sort_order INTEGER DEFAULT 0,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    parent_id BIGINT REFERENCES categories(id)
);

COMMENT ON TABLE categories IS '图书分类表';
COMMENT ON COLUMN categories.id IS '主键ID';
COMMENT ON COLUMN categories.created_time IS '创建时间';
COMMENT ON COLUMN categories.updated_time IS '更新时间';
COMMENT ON COLUMN categories.is_deleted IS '是否删除';
COMMENT ON COLUMN categories.name IS '分类名称';
COMMENT ON COLUMN categories.code IS '分类编码';
COMMENT ON COLUMN categories.description IS '分类描述';
COMMENT ON COLUMN categories.icon IS '分类图标';
COMMENT ON COLUMN categories.sort_order IS '排序权重';
COMMENT ON COLUMN categories.enabled IS '是否启用';
COMMENT ON COLUMN categories.parent_id IS '父分类ID';

-- 图书标签表
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    name VARCHAR(50) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(200),
    color VARCHAR(20),
    usage_count INTEGER DEFAULT 0,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

COMMENT ON TABLE tags IS '图书标签表';
COMMENT ON COLUMN tags.id IS '主键ID';
COMMENT ON COLUMN tags.created_time IS '创建时间';
COMMENT ON COLUMN tags.updated_time IS '更新时间';
COMMENT ON COLUMN tags.is_deleted IS '是否删除';
COMMENT ON COLUMN tags.name IS '标签名称';
COMMENT ON COLUMN tags.code IS '标签编码';
COMMENT ON COLUMN tags.description IS '标签描述';
COMMENT ON COLUMN tags.color IS '标签颜色';
COMMENT ON COLUMN tags.usage_count IS '使用次数';
COMMENT ON COLUMN tags.enabled IS '是否启用';

-- 图书表
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    title VARCHAR(200) NOT NULL,
    subtitle VARCHAR(200),
    author VARCHAR(100) NOT NULL,
    description VARCHAR(2000),
    cover_url VARCHAR(500),
    file_url VARCHAR(500),
    file_size BIGINT,
    file_format VARCHAR(20),
    page_count INTEGER,
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    rating DOUBLE PRECISION DEFAULT 0.0,
    sales_count INTEGER DEFAULT 0,
    stock_count INTEGER DEFAULT 0,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    category_id BIGINT REFERENCES categories(id)
);

COMMENT ON TABLE books IS '图书表';
COMMENT ON COLUMN books.id IS '主键ID';
COMMENT ON COLUMN books.created_time IS '创建时间';
COMMENT ON COLUMN books.updated_time IS '更新时间';
COMMENT ON COLUMN books.is_deleted IS '是否删除';
COMMENT ON COLUMN books.title IS '图书标题';
COMMENT ON COLUMN books.subtitle IS '图书副标题';
COMMENT ON COLUMN books.author IS '作者';
COMMENT ON COLUMN books.description IS '图书简介';
COMMENT ON COLUMN books.cover_url IS '图书封面图片URL';
COMMENT ON COLUMN books.file_url IS '图书文件URL';
COMMENT ON COLUMN books.file_size IS '文件大小(字节)';
COMMENT ON COLUMN books.file_format IS '文件格式';
COMMENT ON COLUMN books.page_count IS '页数';
COMMENT ON COLUMN books.price IS '价格';
COMMENT ON COLUMN books.original_price IS '原价';
COMMENT ON COLUMN books.rating IS '评级';
COMMENT ON COLUMN books.sales_count IS '累计销量';
COMMENT ON COLUMN books.stock_count IS '库存数量';
COMMENT ON COLUMN books.published IS '上架状态';
COMMENT ON COLUMN books.category_id IS '分类ID';

-- 图书标签关联表
CREATE TABLE book_tags (
    book_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (book_id, tag_id)
);

COMMENT ON TABLE book_tags IS '图书标签关联表';
COMMENT ON COLUMN book_tags.book_id IS '图书ID';
COMMENT ON COLUMN book_tags.tag_id IS '标签ID';

-- 订单表
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    pay_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    payment_method VARCHAR(20),
    pay_time TIMESTAMP,
    remark VARCHAR(500)
);

COMMENT ON TABLE orders IS '订单表';
COMMENT ON COLUMN orders.id IS '主键ID';
COMMENT ON COLUMN orders.created_time IS '创建时间';
COMMENT ON COLUMN orders.updated_time IS '更新时间';
COMMENT ON COLUMN orders.is_deleted IS '是否删除';
COMMENT ON COLUMN orders.order_no IS '订单号';
COMMENT ON COLUMN orders.user_id IS '用户ID';
COMMENT ON COLUMN orders.total_amount IS '订单总金额';
COMMENT ON COLUMN orders.discount_amount IS '优惠金额';
COMMENT ON COLUMN orders.pay_amount IS '实付金额';
COMMENT ON COLUMN orders.status IS '订单状态';
COMMENT ON COLUMN orders.payment_method IS '支付方式';
COMMENT ON COLUMN orders.pay_time IS '支付时间';
COMMENT ON COLUMN orders.remark IS '订单备注';

-- 订单项表
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    order_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL
);

COMMENT ON TABLE order_items IS '订单项表';
COMMENT ON COLUMN order_items.id IS '主键ID';
COMMENT ON COLUMN order_items.created_time IS '创建时间';
COMMENT ON COLUMN order_items.updated_time IS '更新时间';
COMMENT ON COLUMN order_items.is_deleted IS '是否删除';
COMMENT ON COLUMN order_items.order_id IS '订单ID';
COMMENT ON COLUMN order_items.book_id IS '图书ID';
COMMENT ON COLUMN order_items.quantity IS '购买数量';
COMMENT ON COLUMN order_items.unit_price IS '单价';
COMMENT ON COLUMN order_items.subtotal IS '小计金额';

-- 用户账户表
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT NOT NULL UNIQUE,
    balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    available_balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    frozen_balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_recharge DECIMAL(10,2) DEFAULT 0.00,
    total_consumption DECIMAL(10,2) DEFAULT 0.00,
    status VARCHAR(20) NOT NULL
);

COMMENT ON TABLE accounts IS '用户账户表';
COMMENT ON COLUMN accounts.id IS '主键ID';
COMMENT ON COLUMN accounts.created_time IS '创建时间';
COMMENT ON COLUMN accounts.updated_time IS '更新时间';
COMMENT ON COLUMN accounts.is_deleted IS '是否删除';
COMMENT ON COLUMN accounts.user_id IS '用户ID';
COMMENT ON COLUMN accounts.balance IS '账户余额';
COMMENT ON COLUMN accounts.available_balance IS '可用余额';
COMMENT ON COLUMN accounts.frozen_balance IS '冻结余额';
COMMENT ON COLUMN accounts.total_recharge IS '累计充值金额';
COMMENT ON COLUMN accounts.total_consumption IS '累计消费金额';
COMMENT ON COLUMN accounts.status IS '账户状态';

-- 账户流水表
CREATE TABLE account_transactions (
    id BIGSERIAL PRIMARY KEY,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT NOT NULL,
    transaction_no VARCHAR(50) NOT NULL UNIQUE,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    balance_before DECIMAL(10,2),
    balance_after DECIMAL(10,2),
    content VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    related_order_no VARCHAR(50),
    transaction_time TIMESTAMP NOT NULL,
    remark VARCHAR(500)
);

COMMENT ON TABLE account_transactions IS '账户流水表';
COMMENT ON COLUMN account_transactions.id IS '主键ID';
COMMENT ON COLUMN account_transactions.created_time IS '创建时间';
COMMENT ON COLUMN account_transactions.updated_time IS '更新时间';
COMMENT ON COLUMN account_transactions.is_deleted IS '是否删除';
COMMENT ON COLUMN account_transactions.user_id IS '用户ID';
COMMENT ON COLUMN account_transactions.transaction_no IS '交易流水号';
COMMENT ON COLUMN account_transactions.transaction_type IS '交易类型';
COMMENT ON COLUMN account_transactions.amount IS '交易金额';
COMMENT ON COLUMN account_transactions.balance_before IS '交易前余额';
COMMENT ON COLUMN account_transactions.balance_after IS '交易后余额';
COMMENT ON COLUMN account_transactions.content IS '交易内容';
COMMENT ON COLUMN account_transactions.status IS '交易状态';
COMMENT ON COLUMN account_transactions.related_order_no IS '关联订单号';
COMMENT ON COLUMN account_transactions.transaction_time IS '交易时间';
COMMENT ON COLUMN account_transactions.remark IS '备注';

-- 用户书架表
CREATE TABLE user_bookshelves (
    id BIGSERIAL PRIMARY KEY,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    reading_progress INTEGER DEFAULT 0,
    reading_percentage DOUBLE PRECISION DEFAULT 0.0,
    last_read_time TIMESTAMP,
    is_favorite BOOLEAN NOT NULL DEFAULT FALSE,
    favorite_time TIMESTAMP,
    reading_duration BIGINT DEFAULT 0,
    remark VARCHAR(500)
);

COMMENT ON TABLE user_bookshelves IS '用户书架表';
COMMENT ON COLUMN user_bookshelves.id IS '主键ID';
COMMENT ON COLUMN user_bookshelves.created_time IS '创建时间';
COMMENT ON COLUMN user_bookshelves.updated_time IS '更新时间';
COMMENT ON COLUMN user_bookshelves.is_deleted IS '是否删除';
COMMENT ON COLUMN user_bookshelves.user_id IS '用户ID';
COMMENT ON COLUMN user_bookshelves.book_id IS '图书ID';
COMMENT ON COLUMN user_bookshelves.reading_progress IS '阅读进度(页码)';
COMMENT ON COLUMN user_bookshelves.reading_percentage IS '阅读百分比';
COMMENT ON COLUMN user_bookshelves.last_read_time IS '最后阅读时间';
COMMENT ON COLUMN user_bookshelves.is_favorite IS '是否收藏';
COMMENT ON COLUMN user_bookshelves.favorite_time IS '收藏时间';
COMMENT ON COLUMN user_bookshelves.reading_duration IS '阅读时长(秒)';
COMMENT ON COLUMN user_bookshelves.remark IS '备注';

-- 字典表
CREATE TABLE dict (
    id BIGSERIAL PRIMARY KEY,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    type VARCHAR(50) NOT NULL,
    label VARCHAR(100) NOT NULL,
    value VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    sort INTEGER NOT NULL DEFAULT 0,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

COMMENT ON TABLE dict IS '字典表';
COMMENT ON COLUMN dict.id IS '主键ID';
COMMENT ON COLUMN dict.created_time IS '创建时间';
COMMENT ON COLUMN dict.updated_time IS '更新时间';
COMMENT ON COLUMN dict.is_deleted IS '是否删除';
COMMENT ON COLUMN dict.type IS '字典类型';
COMMENT ON COLUMN dict.label IS '字典标签';
COMMENT ON COLUMN dict.value IS '字典值';
COMMENT ON COLUMN dict.description IS '描述';
COMMENT ON COLUMN dict.sort IS '排序';
COMMENT ON COLUMN dict.enabled IS '是否启用';

-- 索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_category_id ON books(category_id);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_order_no ON orders(order_no);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_book_id ON order_items(book_id);
CREATE INDEX idx_account_transactions_user_id ON account_transactions(user_id);
CREATE INDEX idx_account_transactions_transaction_no ON account_transactions(transaction_no);
CREATE INDEX idx_user_bookshelves_user_id ON user_bookshelves(user_id);
CREATE INDEX idx_user_bookshelves_book_id ON user_bookshelves(book_id);
CREATE INDEX idx_categories_code ON categories(code);
CREATE INDEX idx_tags_code ON tags(code);
CREATE INDEX idx_dict_type ON dict(type);
CREATE INDEX idx_dict_value ON dict(value);
CREATE UNIQUE INDEX uk_dict_type_value ON dict(type, value);