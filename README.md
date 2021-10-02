# our policy's read me


## 程式介紹
::: spoiler

### /app_manager/Start.java
主程式
#### 參數
1.iter:負責管理訓練epoch數
2.max_iter:每個epoch訓練的步數，每30秒訓練一次
3.input_file:決定訓練用的workload
4.last_qvalue:儲存上次epoch的cost(未改名)，負責決定是否收斂

### /app_manager/restart.java
每個epoch開始時更新Docker環境
1.docker stck rm app(移除docker stack)
2.docker stack deploy --compose-file docker-compose.yml(重新部署docker環境)

### /app_manager/iter_timer.java
負責記錄目前epoch的訓練步數
將目前的訓練步數儲存為iter.txt

### /app_manager/health_check.java
負責確認service是否正常運作
add_c():紀錄service的IP位置，這邊的IP會隨著docker 分配到的IP而改變
check():傳送request到service判斷是否正常運作，出現異常時透過改變signal.txt作為service異常的訊號，暫停系統運作並利用restart.java重新部署service
write_error_count():記錄錯誤次數(不影響系統運作)

### ql3/get_all_use.java
負責在每次學習步驟開始時獲取當下service的cpu utilization
read_iter():讀取目前的訓練步數
get_use1():讀取service目前的cpu utilization，透過讀取每個virtual machine上service的cpu utilization做平均得出cpu utilization

### send_req/new_send_request.java
作為傳送requests的程式
read():讀取要做為workload的檔案
add_stage():加入要傳送的First level MNCSE的stage
send():執行send_req()程式傳送request，這邊使用FixedThreadPool=40，讓他可同時傳送40個request

### send_req/send_req.java
將new_send_request設定好的request傳送出去的程式，
RFID():隨機產生RFID，避免request在傳送到MNCSE時service name重複
send():傳送request到MNCSE，其中，path中的IP要手動修改，修改為Docker swarm的IP(192.168.99.130:666)，Docker swarm microservice 建立方式會另外說明

### ql3/start_ql3.java
Q learning agent的程式，負責設定agent相關設定
ql3:agent主程式
ql3.get_machine_id():獲取VM的ID
ql3.read_qtable():讀取Q table，若沒有Qtable則建立一個
ql3.init_state():初始化Q learning state
stop.read():負責讀取signal.txt，判斷servuce是否有問題，需要暫停程式
ql3.learn():執行Q learning學習步驟，讀取當下service的state，計算可能的action，透過epislon greedy選取action，依照選取的action調整service的資源，並觀察其表現計算cost更新Q table
write()、write_last():記錄在哪個學習步驟時service發生錯誤，並記錄當下狀態，用於觀察當下分配德資源是否足夠，對Q learning運作不影響
ql3.print():再epoch結束後輸出Q table
ql3.avg_qvalue():計算當前epoch的平均cost(cost沒錯，沒有改名而已)

### ql3/ql3.java
init_state():決定Q learning的起始狀態
print():輸出Qtable
print_state_action():每次學習步驟時輸出狀態跟動作
init_5():重製Q table
read_qtable():讀取上個epoch輸出的Q table作為這次的Q talbe，若沒有Q table則新建立一個
get_state2():讀取service的cpus、replicas作為state
calculateQ():學習步驟的核心，
1.呼叫get_state2()或取當下狀態
2.呼叫cla_state()計算當前狀態
3.呼叫possibleActionFromState()計算當前狀態可能action
4.透過epslon greedy方法選取action
5.呼叫container_update()向swarm manager更新service狀態
6.等待25秒讓service順利啟動
7.呼叫cost()計算當下選取的動作的cost
8.更新Q table
9.呼叫print_qvalue()輸出當前狀態動作的cost(輸出cost沒錯，沒有改名而已)
10.將狀態更新為下個狀態
cal_use():判斷當前選的動作會不會造成cpu utilization大於80%，大於80%時不能選取該動作
possibleActionFromState():計算當前狀態下可選擇的action
cla_state():計算當前狀態數值
cost():計算當前狀態下選擇的action的cost，
wres:resource cost所佔的比例
wperf:performance cost所佔的比例
1.呼叫response_time()獲取當前選擇action的service的response time，用於計算當performance cost
2.設定tmax(mn1:25ms、mn2:15ms、mnae:5ms)
3.當response time超過50ms時設定50ms
4.計算peerformance cost、resource cost，二者加總即是當下的cost

container_update():向swarm manager更新當下service的狀態，透過(docker service update指令)
send_request():透過向service傳送request獲取response time，這邊的ip(192.168.99.130)設定為swarm manager的ip，此ip會隨著VMip改變而改變
response_time():透過呼叫send_request()獲取response time，我們用3個request的平均做為這次的response time
print_res_time():紀錄每次學習步驟的response time
print_res_time2():輸出當下的response time(只有1比)
print_share():紀錄每次學習步驟的cpus
print_use():紀錄每次學習步驟的cpu utilization
print_qvalue():紀錄每次學習步驟的cost
print_res_cost():紀錄每次學習步驟的resource cost
print_perf_cost():紀錄每次學習步驟的performance cost
get_machine_id():獲取VM的ID
docker_api_update():更新service的cpus
avg_qvalue():計算這次epoch的平均cost
print_avg_qvalue():輸出這次epoch的平均cost

class State():作為空白狀態的class

### read_qvalue
讀取上次epoch的cost，用於計算Q learning是否收斂


:::

## 啟動步驟





















