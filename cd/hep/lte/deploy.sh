shell
#!/bin/bash

filename="./saas/api/admin"
backup_dir="./saas/api/admin/backup"

# 检查备份目录是否存在，若不存在则创建
if [ ! -d "$backup_dir" ]; then
  mkdir "$backup_dir"
fi

# 获取最新的备份文件序号
latest_backup=$(ls "$backup_dir" | grep -E "^$filename-[0-9]+$" | sort -r | head -n 1)

if [ -z "$latest_backup" ]; then
  # 若没有找到备份文件，则序号从1开始
  backup_number=1
else
  # 提取最新备份文件的序号
  backup_number=$(echo "$latest_backup" | awk -F'-' '{print $NF}')
  backup_number=$((backup_number + 1))
fi

# 构建备份文件名
backup_filename="$filename-$backup_number"

# 执行备份操作
cp -p "$filename" "$backup_dir/$backup_filename"

echo "已备份文件: $filename 为 $backup_filename"

# 如果需要回滚文件
if [ "$1" = "rollback" ]; then
  if [ -n "$2" ]; then
    rollback_number="$2"
    rollback_file="$filename-$rollback_number"

    if [ -f "$backup_dir/$rollback_file" ]; then
      # 去掉序号的新文件名
      new_filename=$(echo "$filename" | awk -F'-' '{$NF=""; print $0}' | sed 's/-$//')

      # 恢复文件
      cp "$backup_dir/$rollback_file" "$new_filename"
      echo "已回滚文件: $new_filename"
    else
      echo "指定的序号备份文件不存在"
    fi
  else
    echo "请指定要回滚的备份文件的序号"
  fi
fi