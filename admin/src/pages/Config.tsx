import React from 'react';
import { Table, Button, Space } from 'antd';

const Config: React.FC = () => {
  const columns = [
    { title: 'Key', dataIndex: 'configKey', key: 'configKey' },
    { title: '名称', dataIndex: 'name', key: 'name' },
    { title: '值', dataIndex: 'value', key: 'value' },
    { title: '分类', dataIndex: 'category', key: 'category' },
    {
      title: '操作',
      key: 'action',
      render: () => (
        <Space>
          <Button type="link">编辑</Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <h2>系统参数</h2>
      <Table columns={columns} dataSource={[]} rowKey="id" />
    </div>
  );
};

export default Config;
