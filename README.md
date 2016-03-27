# Android Map - sebuah aplikasi yang dapat berkomunikasi dengan server dan melakukan potret dengan kamera

## Deskripsi Aplikasi

Aplikasi berikut dapat menemukan beberapa tempat di lingkungan ITB dengan bantuan tools yang dibangun di atas platform Android. Aplikasi ini dapat memandu peserta dengan menampilkan peta (Google Maps) dan arah tujuannya. Akan ada 3 lokasi (akan diberikan dari server). Pada setiap lokasi, aplikasi dapat  mengirimkan nama lokasi tempat peserta berada. 

Daftar kemungkinan jawaban lokasi adalah

* gku_barat
* gku_timur
* intel
* cc_barat
* cc_timur
* dpr
* oktagon
* perpustakaan
* pau
* kubus

Server uji coba adalah 167.205.34.132 dengan port 3111.

## Spesifikasi Aplikasi

* Aplikasi mampu menerima pesan dari server dengan format JSON berisi lokasi dan token.
* Aplikasi mampu mengolah data berupa location point (longitude, latitude) dan menampilkan indicator pada peta lokasi yang dimaksud. Peta lokasi menggunakan Google Map API.
* Aplikasi mampu mengirim intent kamera.
* Terdapat sebuah panah navigasi yang berada di atas peta yang menunjukan arah utara. Navigasi ini menggunakan sensor yang ada pada android API.
* Aplikasi mampu mengambil gambar melalui kamera.
* Aplikasi mampu mengirimkan pesan ke sever dengan format JSON berisi lokasi (longitude, latitude), nim serta token.
* Hasil reply dari server ditampilkan dalam bentuk *alert dialog*.
* Sensor tersebut harus dilepas ketika berpindah activity agar tidak boros baterai.


## Spesifikasi Pertukaran Pesan
Keterangan : *Client* pada dokumen ini adalah aplikasi ini dan *Server* merupakan server milik asisten.
### Request Location
Permintaan lokasi (pertama).

**Client Request**
```sh
{“com”:”req_loc”,”nim”:”13513024”}
```
**Server Response** 
```sh
{“status”:”ok”,”nim”:”13513024”,”longitude”:”6.234123132”,”latitude”:”0.1234123412”,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
### Send Answer
Mengirimkan jawaban dan menerima lokasi berikutnya

**Client Request**
```sh
{“com”:”answer”,”nim”:”13513024”,”answer”:”labtek_v”, ”longitude”:”6.234123132”,”latitude”:”0.1234123412”,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
**Server Response**
Jika jawaban **benar**, maka:
```sh
{“status”:”ok”,”nim”:”13513024”,”longitude”:”8.13215123214”,”latitude”:”9.1234123412”,”token”:”124fewfm32r32ifmwder42”}
```
Jika jawaban **salah**, maka:
```sh
{“status”:”wrong_answer”,”nim”:”13513024”,”token”:”124fewfm32r32ifmwder42”}
```
Jika jawaban **benar dan sudah berada di lokasi ketiga**, maka:
```sh
{“status”:”finish”,”nim”:”13513024”,”token”:”124fewfm32r32ifmwder42”,”check”:1}
```
## Spesifikasi Tampilan
**Tampilan peta**

![alt text](http://i.imgur.com/igpBp8P.jpg)

**Tampilan *submit* jawaban**

![alt text](http://i.imgur.com/KwgM7cd.jpg)

## Lokasi Source Code
Source code Java terletak pada direktori berikut.
```sh
/AndroidMap/app/src/main/java/com/luqman/androidmap
```
Source code XML terletak pada direktori berikut.
```sh
/AndroidMap/app/src/main/res/layout
```

## Lokasi binary file
Binary file (ekstensi .apk) aplikasi Android Map ini terletak pada direktori berikut.
```sh
/AndroidMap/bin
```

[Markdown]: <http://dillinger.io/>
