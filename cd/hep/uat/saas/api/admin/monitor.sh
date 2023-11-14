#CPU、磁盘、内存使用率、带宽
system=0
user=0
i=0
log="/findsoft/shell/monitor.txt"
#带宽使用情况
time=`date "+%Y-%m-%d %k:%M"`
day=`date "+%Y-%m-%d"`
minute=`date "+%k:%M"`
echo  "*************************************************************************" >> LOG.txt
echo "统计开始时间：$day $minute" >>$log

#循环五次，避免看到的是偶然的数据
echo "#带宽的使用情况：#" >>$log
while (( $i<5 ))
do
#原先的`ifconfig eth0|sed -n "7p"|awk '{print $2}'|cut -c7-`方式获取网卡的信息为空，已经注释掉
#rx_before=`ifconfig eth0|sed -n "7p"|awk '{print $2}'|cut -c7-`
#tx_before=`ifconfig eth0|sed -n "7p"|awk '{print $6}'|cut -c7-`
rx_before=$(cat /proc/net/dev | grep 'eth' | tr : " " | awk '{print $2}')
tx_before=$(cat /proc/net/dev | grep 'eth' | tr : " " | awk '{print $10}')
sleep 2
#rx_after=`ifconfig eth0|sed -n "7p"|awk '{print $2}'|cut -c7-`
#tx_after=`ifconfig eth0|sed -n "7p"|awk '{print $6}'|cut -c7-`
rx_after=$(cat /proc/net/dev | grep 'eth' | tr : " " | awk '{print $2}')
tx_after=$(cat /proc/net/dev | grep 'eth' | tr : " " | awk '{print $10}')

rx_result=$[(rx_after-rx_before)/1024/1024/2*8]
tx_result=$[(tx_after-tx_before)/1024/1024/2*8]
echo  "$time Now_In_Speed: $rx_result Mbps Now_OUt_Speed: $tx_result Mbps" >>$log

let "i++"
done

rx_result=$(cat 123.txt|grep "$time"|awk '{In+=$4}END{print In}')
tx_result=$(cat 123.txt|grep "$time"|awk '{Out+=$7}END{print Out}')
In_Speed=$(echo "scale=2;$rx_result/5"|bc)
Out_Speed=$(echo "scale=2;$tx_result/5"|bc)
#echo "#带宽的5次的平均值是：#" >>123.txt
echo  "$time In_Speed_average: $In_Speed Mbps Out_Speed_average: $Out_Speed Mbps" >>$log

#CPU使用情况
cpuuse=$(cat /proc/loadavg | awk '{print $1}')
if [ "$cpuuse" > 0 ]; then
SUBJECT="ATTENTION: CPU Load Is High on $(hostname) at $(date)"
TO="282785755@qq.com"
echo "CPU Current Usage is: $cpuuse%" >>$log
echo "" >>$log
echo "+-------------------------------------+" >>$log
echo "Top CPU Process Using top command" >>$log
echo "+-------------------------------------+" >>$log
echo "$(top -bn1 | head -20)" >>$log
echo "" >>$log
echo "+-------------------------------------+" >>$log
echo "Top CPU Process Using ps command" >>$log
echo "+-------------------------------------+" >>$log
echo "$(ps -eo pcpu,pid,user,args | sort -k 1 -r | head -10)" >>$log
mail -s "$SUBJECT" "$TO" <$log
fi

# top -n 参数指定运行次数，1代表运行一次即停止，不再等待top数据更新，使用awk指定分割符，提取数据
# 默认bash shell不能直接运算小数点，所以需要借助bc
# bc命令是一种支持任意精度的交互执行的计算器语言。
# 常见用法 echo "1.23*5" | bc

#磁盘使用情况(注意：需要用sed先进行格式化才能进行累加处理)
disk_used=$(df -m | sed '1d;/ /!N;s/\n//;s/ \+/ /;' | awk '{used+=$3} END{print used}')
disk_totalSpace=$(df -m | sed '1d;/ /!N;s/\n//;s/ \+/ /;' | awk '{totalSpace+=$2} END{print totalSpace}')
disk_all=$(echo "scale=4;$disk_used/$disk_totalSpace" | bc)
disk_percent1=$(echo $disk_all | cut -c 2-3)
disk_percent2=$(echo $disk_all | cut -c 4-5)
disk_warning=`df -m | sed '1d;/ /!N;s/\n//;s/ \+/ /;' | awk '{if ($5>85) print $5 $6;} '`
echo "#磁盘利用率#" >>$log
echo "hard disk has used: $disk_percent1.$disk_percent2%" >>$log
echo -e "\t\t#磁盘存在目录使用率超过85%报警#" >>$log
echo -e "\t\tover used: $disk_warning" >>$log


#内存使用情况
memery_used=$(free -m | awk 'NR==2' | awk '{print $3}')
buffer_used=$(free -m | awk 'NR==2' | awk '{print $6}')
cache_used=$(free -m | awk 'NR==2' | awk '{print $7}')
free=$(free -m | awk 'NR==2' | awk '{printf $4}')
memery_all=$(free -m | awk 'NR==2' | awk '{print $2}')
used_all=$[memery_all-(free+buffer_used+cache_used)]
echo "$used_all $memery_all $free" >>$log
memery_percent=$(echo "scale=4;$memery_used / $memery_all" | bc)
memery_percent2=$(echo "scale=4; $used_all / $memery_all" | bc)
percent_part1=$(echo $memery_percent | cut -c 2-3)
percent_part2=$(echo $memery_percent | cut -c 4-5)
percent_part11=$(echo $memery_percent2 | cut -c 2-3)
percent_part22=$(echo $memery_percent2 | cut -c 4-5)
echo "#内存使用率#" >> $log
echo "system memery is already use: $percent_part1.$percent_part2%" >>$log
echo "actual memery is already use: $percent_part11.$percent_part22%" >>$log

echo  "结束本次统计：$day $minute" >> $log
echo  "*************************************************************************" >> $log
echo -e "\n\n\n\n" >> $log

