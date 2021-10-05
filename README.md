# dynamic threshold readme

## 程式介紹

::: spoiler

### start.java
主程式，
參數:
iter:訓練的epoch數(一次訓練多個epoch時資料會存在同一個txt當中，要自行分開)
max_iter:每個epoch的學習步數
input_file:訓練用的workload file

### restart.java
每個epoch開始時更新Docker環境 1.docker stck rm app(移除docker stack) 2.docker stack deploy --compose-file docker-compose.yml(重新部署docker環境)
### get_all_use.java
取得學習過程中各個service的cpu utilization
read_iter():讀取目前的訓練步數
get_use1():讀取service目前的cpu utilization，透過讀取每個virtual machine上service的cpu utilization做平均得出cpu utilization
### get_all_res.java 
取得學習過程中各個service的response time
responses_time():負責取得response time，我們用3次response time的平均做為這次的response time，當傳送的request的http response code非201就重傳一次request
當response time超過50ms時回傳50ms
send_request():對各個service發送request，這邊的IP指定swarm manager的IP，需要手動修改
print_res_time():輸出並記錄response time
print_res_time2():輸出當前的response time(只保存1筆，方便q learning使用)
### new_send_request.java
作為傳送requests的程式
read():讀取要做為workload的檔案
add_stage():加入要傳送的First level MNCSE的stage
send():執行send_req()程式傳送request，這邊使用FixedThreadPool=40，讓他可同時傳送40個request
### send_request.java
將new_send_request設定好的request傳送出去的程式，
RFID():隨機產生RFID，避免request在傳送到MNCSE時service name重複
send():傳送request到MNCSE，其中，path中的IP要手動修改，修改為Docker swarm的IP(192.168.99.130:666)，Docker swarm microservice 建立方式會另外說明
### health_check.java
負責確認service是否正常運作
add_c():紀錄service的IP位置，這邊的IP會隨著docker 分配到的IP而改變
check():傳送request到service判斷是否正常運作，出現異常時透過改變signal.txt作為service異常的訊號，暫停系統運作並利用restart.java重新部署service
write_error_count():記錄錯誤次數(不影響系統運作)
### service.java
dynamic threshold q learning的agent
add_machine():新增docker machine service name
read_qtable():讀取上次epoch的q table，若沒有上次的q table則建立1空的q table
init_state():初始化q learning state
calculateQ():q learning學習步驟
1.執行get_use2()，取得當前service的cpu utilization
2.結合scaling threshold一起計算當下的state
3.執行possibleActionsFromCurrentState()，計算當前可能action，並透過epsilon greedy 選取action
4.根據選取的action更新scaling threshold
5.根據新的scaling threshold跟cpu utilization判斷要不要做scale-out 動作，並更新當前service的replicas數
6.執行cost()，計算當下的cost
7.更新q table
print_cost():記錄學習步驟的cost
get_use2():取得當前service的cpu utilization
get_cons():取得當前service的replicas
add_cons():更新service的replicas

:::

## 啟動步驟
1.建立Docker 環境[參考這篇](https://hackmd.io/bMI9DG9ASmmdvG3h0kN2lA?edit)
2.依照docker machine IP修改程式中的IP，需要修改的有
get_all_res.java、send_req.java、health_checl.java
3.設定start.java當中的參數
iter:訓練的epoch數
max_iter:每個epoch的學習步數
input_file:訓練的workload
4.設定service.java相關參數
state:可能的狀態數
action:可能的動作
container:service最大可用replicas
alpha:learning rate
gamma:
init_state():q learning的初始狀態，可調整service初始的scaling threshold

















