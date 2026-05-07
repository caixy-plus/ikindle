package com.ikindle.config;

import com.ikindle.entity.*;
import com.ikindle.repository.*;
import com.ikindle.service.AccountService;
import com.ikindle.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;

/**
 * 数据初始化配置
 * 在应用启动时插入测试数据
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PermissionRepository permissionRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private DictRepository dictRepository;
    @Autowired private MenuRepository menuRepository;
    @Autowired private SyncSettingRepository syncSettingRepository;
    @Autowired private AccountService accountService;
    @Autowired private SystemConfigService systemConfigService;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initPermissions();
        initRoles();
        initUsers();
        initAccounts();
        initCategories();
        initTags();
        initBooks();
        initDicts();
        initMenus();
        initSystemConfig();
        initSyncSettings();
    }

    private void initPermissions() {
        if (permissionRepository.count() == 0) {
            Permission userRead = createPermission("USER_READ", "用户查看", Permission.PermissionType.API, "/api/users/**", "GET");
            Permission userWrite = createPermission("USER_WRITE", "用户管理", Permission.PermissionType.API, "/api/users/**", "POST,PUT,DELETE");
            Permission bookRead = createPermission("BOOK_READ", "图书查看", Permission.PermissionType.API, "/api/books/**", "GET");
            Permission bookWrite = createPermission("BOOK_WRITE", "图书管理", Permission.PermissionType.API, "/api/books/**", "POST,PUT,DELETE");
            Permission orderRead = createPermission("ORDER_READ", "订单查看", Permission.PermissionType.API, "/api/orders/**", "GET");
            Permission orderWrite = createPermission("ORDER_WRITE", "订单管理", Permission.PermissionType.API, "/api/orders/**", "POST,PUT,DELETE");
            Permission accountManage = createPermission("ACCOUNT_MANAGE", "账户管理", Permission.PermissionType.API, "/api/account/**", "GET,POST,PUT");
            Permission menuManage = createPermission("MENU_MANAGE", "菜单管理", Permission.PermissionType.MENU, "/admin/menus", "GET,POST,PUT,DELETE");
            Permission configManage = createPermission("CONFIG_MANAGE", "配置管理", Permission.PermissionType.API, "/api/system-config/**", "GET,POST,PUT,DELETE");
            permissionRepository.saveAll(Arrays.asList(userRead, userWrite, bookRead, bookWrite,
                    orderRead, orderWrite, accountManage, menuManage, configManage));
        }
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("系统管理员");
            adminRole.setPermissions(new HashSet<>(permissionRepository.findAll()));
            roleRepository.save(adminRole);

            Role userRole = new Role();
            userRole.setName("USER");
            userRole.setDescription("普通用户");
            userRole.setPermissions(new HashSet<>(Arrays.asList(
                    permissionRepository.findByName("USER_READ").orElse(null),
                    permissionRepository.findByName("BOOK_READ").orElse(null),
                    permissionRepository.findByName("ORDER_READ").orElse(null)
            )));
            roleRepository.save(userRole);
        }
    }

    private void initUsers() {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("系统管理员");
            admin.setEmail("admin@ikindle.com");
            admin.setEnabled(true);
            admin.setRoles(new HashSet<>(Arrays.asList(roleRepository.findByName("ADMIN").orElse(null))));
            userRepository.save(admin);

            User testUser = new User();
            testUser.setUsername("test");
            testUser.setPassword(passwordEncoder.encode("test123"));
            testUser.setNickname("测试用户");
            testUser.setEmail("test@ikindle.com");
            testUser.setEnabled(true);
            testUser.setRoles(new HashSet<>(Arrays.asList(roleRepository.findByName("USER").orElse(null))));
            userRepository.save(testUser);
        }
    }

    private void initAccounts() {
        userRepository.findAll().forEach(u -> accountService.createForUser(u.getId()));
    }

    private void initCategories() {
        if (categoryRepository.count() == 0) {
            Category tech = createCategory("技术", "tech", "技术类图书", 1, null);
            Category literature = createCategory("文学", "literature", "文学类图书", 2, null);
            Category history = createCategory("历史", "history", "历史类图书", 3, null);
            categoryRepository.save(tech);
            categoryRepository.save(literature);
            categoryRepository.save(history);

            Category programming = createCategory("编程", "programming", "编程技术", 1, tech);
            categoryRepository.save(programming);
        }
    }

    private void initTags() {
        if (tagRepository.count() == 0) {
            Tag js = createTag("JavaScript", "js", "JavaScript相关", "#1890ff");
            Tag react = createTag("React", "react", "React相关", "#52c41a");
            Tag ts = createTag("TypeScript", "ts", "TypeScript相关", "#722ed1");
            Tag nodejs = createTag("Node.js", "nodejs", "Node.js相关", "#fa8c16");
            tagRepository.saveAll(Arrays.asList(js, react, ts, nodejs));
        }
    }

    private void initBooks() {
        if (bookRepository.count() == 0) {
            Category techCategory = categoryRepository.findByCode("tech");
            Tag jsTag = tagRepository.findByCode("js");
            Tag reactTag = tagRepository.findByCode("react");

            Book js = new Book();
            js.setTitle("JavaScript高级程序设计");
            js.setSubtitle("第4版");
            js.setAuthor("Nicholas C. Zakas");
            js.setDescription("JavaScript经典教程,深入浅出地介绍了 JavaScript 的核心概念和高级特性。");
            js.setPrice(new BigDecimal("89.00"));
            js.setOriginalPrice(new BigDecimal("99.00"));
            js.setRating(4.8);
            js.setSalesCount(1250);
            js.setStockCount(100);
            js.setPublished(true);
            js.setCategory(techCategory);
            js.setTags(new HashSet<>(Arrays.asList(jsTag)));
            bookRepository.save(js);

            Book react = new Book();
            react.setTitle("React 学习手册");
            react.setSubtitle("第2版");
            react.setAuthor("Alex Banks");
            react.setDescription("从零开始学习 React,包含最新的 Hooks 和函数组件特性。");
            react.setPrice(new BigDecimal("79.00"));
            react.setOriginalPrice(new BigDecimal("89.00"));
            react.setRating(4.6);
            react.setSalesCount(980);
            react.setStockCount(80);
            react.setPublished(true);
            react.setCategory(techCategory);
            react.setTags(new HashSet<>(Arrays.asList(reactTag)));
            bookRepository.save(react);
        }
    }

    private void initDicts() {
        if (dictRepository.count() == 0) {
            dictRepository.save(createDict("user_status", "正常", "1", "用户状态正常", 1));
            dictRepository.save(createDict("user_status", "禁用", "0", "用户被禁用", 2));
            dictRepository.save(createDict("gender", "男", "1", "男性", 1));
            dictRepository.save(createDict("gender", "女", "2", "女性", 2));
            dictRepository.save(createDict("payment_method", "支付宝", "ALIPAY", "支付宝支付", 1));
            dictRepository.save(createDict("payment_method", "微信支付", "WECHAT", "微信支付", 2));
            dictRepository.save(createDict("payment_method", "余额支付", "BALANCE", "账户余额支付", 3));
        }
    }

    /**
     * 默认菜单 - 对应原型图底部 tab + 个人中心入口 + 管理后台
     */
    private void initMenus() {
        if (menuRepository.count() > 0) return;

        // 用户端底部 tab
        Menu home = createMenu("home", "首页", "🏠", "/home", null, 1, Menu.MenuType.USER);
        Menu shelf = createMenu("bookshelf", "书架", "📚", "/bookshelf", null, 2, Menu.MenuType.USER);
        Menu category = createMenu("category", "分类", "🗂️", "/category", null, 3, Menu.MenuType.USER);
        Menu profile = createMenu("profile", "我的", "👤", "/profile", null, 4, Menu.MenuType.USER);
        menuRepository.saveAll(Arrays.asList(home, shelf, category, profile));

        // 个人中心子菜单
        Long profileId = profile.getId();
        menuRepository.saveAll(Arrays.asList(
                createMenu("my_orders", "我的订单", "🧾", "/profile/orders", profileId, 1, Menu.MenuType.USER),
                createMenu("my_recharge", "充值记录", "💰", "/profile/recharge", profileId, 2, Menu.MenuType.USER),
                createMenu("my_sync", "我的同步", "🔄", "/profile/sync", profileId, 3, Menu.MenuType.USER),
                createMenu("my_settings", "Kindle 设置", "⚙️", "/profile/sync-settings", profileId, 4, Menu.MenuType.USER),
                createMenu("about", "关于", "ℹ️", "/profile/about", profileId, 5, Menu.MenuType.USER),
                createMenu("logout", "退出登录", "🚪", "/profile/logout", profileId, 6, Menu.MenuType.USER)
        ));

        // 管理后台菜单
        Menu adminDashboard = createMenu("admin_dashboard", "仪表盘", "📊", "/admin/dashboard", null, 1, Menu.MenuType.ADMIN);
        Menu adminBooks = createMenu("admin_books", "图书管理", "📖", "/admin/books", null, 2, Menu.MenuType.ADMIN);
        Menu adminCategories = createMenu("admin_categories", "分类管理", "🗂️", "/admin/categories", null, 3, Menu.MenuType.ADMIN);
        Menu adminTags = createMenu("admin_tags", "标签管理", "🏷️", "/admin/tags", null, 4, Menu.MenuType.ADMIN);
        Menu adminOrders = createMenu("admin_orders", "订单管理", "🛒", "/admin/orders", null, 5, Menu.MenuType.ADMIN);
        Menu adminUsers = createMenu("admin_users", "用户管理", "👥", "/admin/users", null, 6, Menu.MenuType.ADMIN);
        Menu adminRoles = createMenu("admin_roles", "角色权限", "🔐", "/admin/roles", null, 7, Menu.MenuType.ADMIN);
        Menu adminAccounts = createMenu("admin_accounts", "账户管理", "💳", "/admin/accounts", null, 8, Menu.MenuType.ADMIN);
        Menu adminDicts = createMenu("admin_dicts", "字典管理", "📚", "/admin/dicts", null, 9, Menu.MenuType.ADMIN);
        Menu adminConfig = createMenu("admin_config", "系统参数", "⚙️", "/admin/config", null, 10, Menu.MenuType.ADMIN);
        Menu adminSync = createMenu("admin_sync", "同步任务", "🔄", "/admin/sync", null, 11, Menu.MenuType.ADMIN);
        Menu adminMenus = createMenu("admin_menus", "菜单管理", "🧭", "/admin/menus", null, 12, Menu.MenuType.ADMIN);
        menuRepository.saveAll(Arrays.asList(adminDashboard, adminBooks, adminCategories, adminTags,
                adminOrders, adminUsers, adminRoles, adminAccounts, adminDicts, adminConfig, adminSync, adminMenus));
    }

    private void initSystemConfig() {
        registerConfig("jwt.expiration", "JWT 过期时间(毫秒)", ConfigDefinition.ValueType.NUMBER, "86400000", "system");
        registerConfig("oss.enabled", "OSS 是否启用", ConfigDefinition.ValueType.BOOLEAN, "false", "storage");
        registerConfig("upload.max-size", "上传文件最大大小(字节)", ConfigDefinition.ValueType.NUMBER, "52428800", "storage");
        registerConfig("recharge.min-amount", "最低充值金额", ConfigDefinition.ValueType.NUMBER, "10", "account");
        registerConfig("sync.poll-interval", "同步任务轮询间隔(毫秒)", ConfigDefinition.ValueType.NUMBER, "30000", "sync");
        registerConfig("site.title", "站点名称", ConfigDefinition.ValueType.STRING, "iKindle 电子书商城", "ui");
        registerConfig("site.banner-text", "首页 banner 文案", ConfigDefinition.ValueType.STRING, "知识无界,阅读不停", "ui");
        // OAuth 平台配置
        registerConfig("oauth.enabled", "OAuth 登录是否启用", ConfigDefinition.ValueType.BOOLEAN, "false", "auth");
        registerConfig("oauth.provider-name", "OAuth 提供商名称", ConfigDefinition.ValueType.STRING, "app_plat", "auth");
        registerConfig("oauth.base-url", "OAuth 平台基础地址", ConfigDefinition.ValueType.STRING, "http://localhost:8080/api", "auth");
    }

    private void initSyncSettings() {
        userRepository.findAll().forEach(u -> {
            if (!syncSettingRepository.existsByUserId(u.getId())) {
                SyncSetting setting = new SyncSetting();
                setting.setUser(u);
                setting.setAutoSync(true);
                setting.setPriority(SyncSetting.Priority.NORMAL);
                setting.setPreferredFormat("EPUB");
                if ("admin".equals(u.getUsername())) {
                    setting.setKindleEmail("admin@kindle.com");
                }
                syncSettingRepository.save(setting);
            }
        });
    }

    private Permission createPermission(String name, String description, Permission.PermissionType type, String resource, String method) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        permission.setType(type);
        permission.setResource(resource);
        permission.setMethod(method);
        return permission;
    }

    private Tag createTag(String name, String code, String description, String color) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setCode(code);
        tag.setDescription(description);
        tag.setColor(color);
        tag.setEnabled(true);
        tag.setUsageCount(0);
        return tag;
    }

    private Category createCategory(String name, String code, String description, int sort, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setCode(code);
        category.setDescription(description);
        category.setSortOrder(sort);
        category.setEnabled(true);
        category.setParent(parent);
        return category;
    }

    private Dict createDict(String type, String label, String value, String description, int sort) {
        Dict dict = new Dict();
        dict.setType(type);
        dict.setLabel(label);
        dict.setValue(value);
        dict.setDescription(description);
        dict.setSort(sort);
        dict.setEnabled(true);
        return dict;
    }

    private Menu createMenu(String key, String text, String icon, String path, Long parentId, int sort, Menu.MenuType type) {
        Menu menu = new Menu();
        menu.setMenuKey(key);
        menu.setText(text);
        menu.setIcon(icon);
        menu.setPath(path);
        menu.setParentId(parentId);
        menu.setSortOrder(sort);
        menu.setMenuType(type);
        menu.setVisible(true);
        return menu;
    }

    private void registerConfig(String key, String name, ConfigDefinition.ValueType type, String defaultValue, String category) {
        ConfigDefinition def = new ConfigDefinition();
        def.setConfigKey(key);
        def.setName(name);
        def.setValueType(type);
        def.setDefaultValue(defaultValue);
        def.setCategory(category);
        systemConfigService.registerDefinition(def);
    }
}
