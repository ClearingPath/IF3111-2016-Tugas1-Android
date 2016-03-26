# Tugas Besar 1 IF3111 Pengembangan Aplikasi pada Platform Khusus
## GGPS - GoogleMaps Guided Positioning System oleh Jonathan Benedict / 13513003

## Latar Belakang

Dalam tugas ini, peserta diminta untuk menemukan beberapa tempat di lingkungan ITB dengan bantuan tools yang dibangun di atas platform Android. Aplikasi tersebut dapat memandu peserta dengan menampilkan peta (Google Maps) dan arah tujuannya. Peserta diminta menemukan 3 lokasi (akan diberikan dari server). Pada setiap lokasi peserta akan diminta untuk foto-diri di lokasinya dan mengirimkan nama lokasi tempat peserta berada. 

Daftar kemungkinan jawaban lokasi diberikan oleh asisten. Pastikan anda tidak typo saat mengirimkan jawaban ke server. 

Daftar kemungkinan jawaban lokasi adalah

* gku_barat
* gku_timur
* intel
* cc_barat
* cc_timur
* dpr
* sunken
* perpustakaan
* pau
* kubus

Sever uji coba akan disediakan pada 167.205.24.132 akan dapat diuji coba mulai tanggal 21 Maret 2016 Pukul 07.00.
Prosedur uji coba akan dibertahukan lebih lanjut.

## Spesifikasi Aplikasi

Spesifikasi dari aplikasi yang dibangun sebagai berikut

* Aplikasi mampu menerima pesan dari server dengan format JSON berisi lokasi dan token.
* Aplikasi mampu mengolah data berupa location point (longitude, latitude) dan menampilkan indicator pada peta lokasi yang dimaksud. Peta lokasi menggunakan Google Map API.
* Terdapat sebuah panah navigasi yang berada diatas peta , yang menunjukan arah utara yang menggunakan sensor magnetik dan akselerometer.
* Aplikasi mampu mengirim intent kamera.
* Aplikasi mampu mengambil gambar melalui kamera.
* Hasil reply dari server harus ditampilkan dalam bentuk *toast*.
* Ketika aplikasi menggunakan activity yang memanggil sensor, ketika aplikasi berpindah activity maka sensor akan dilepas agar tidak boros baterai.

## Spesifikasi Pertukaran Pesan
Keterangan : *Client* pada dokumen ini adalah aplikasi dan *Server* merupakan server milik asisten.

### Request Location
Permintaan lokasi (pertama).

**Client Request**
```sh
{“com”:”req_loc”,”nim”:”13513003”}
```
**Server Response** 
```sh
{“status”:”ok”,”nim”:”13513003”,”longitude”:6.234123132,”latitude”:0.1234123412,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```

### Send Answer
Mengirimkan jawaban dan menerima lokasi berikutnya

**Client Request**
```sh
{“com”:”answer”,”nim”:”13513003”,”answer”:”labtek_v”, ”longitude”:6.234123132,”latitude”:0.1234123412,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
**Server Response**
Jika jawaban Anda **benar**, maka:
```sh
{“status”:”ok”,”nim”:”13513003”,”longitude”:8.13215123214,”latitude”:9.1234123412,”token”:”124fewfm32r32ifmwder42”}
```
Jika jawaban Anda **salah**, maka:
```sh
{“status”:”wrong_answer”,”nim”:”13513003”,”token”:”124fewfm32r32ifmwder42”}
```
Jika jawaban Anda **benar dan sudah berada dilokasi terakhir**, maka:
```sh
{“status”:”finish”,”nim”:”13513003”,”token”:”124fewfm32r32ifmwder42”,”check”:1}
```

## Spesifikasi Tampilan
**Tampilan horizontal**
![horizontal](/screenshot/Screenshot_horizontal.jpeg "horizontal")
**Tampilan vertikal**
![vertikal](/screenshot/Screenshot_vertikal.jpeg "vertikal")
**Tampilan *submit* jawaban**
![answer](/screenshot/Screenshot_answer.jpeg "answer")

## Source code and binaries
**bin**
[Tubes1-Android\GPS\app\build\outputs\apk](/GPS/app/build/outputs/apk "bin")
**src**
[Tubes1-Android\GPS\app\src\main\java\com\example\user\gps](/GPS/app/src/main/java/com/example/user/gps "source")
**layout xml**
[Tubes1-Android\GPS\app\src\main\res\layout](/GPS/app/src/main/res/layout "layout")
