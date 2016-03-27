# Tugas Besar 1 IF3111 Pengembangan Aplikasi pada Platform Khusus
## Identitas
Nama: Muhammad Nizami
NIM: 13512501
Kelas: 01

## Berkas-berkas
binary: app/build/outputs/apk/*
source code: app/src/**

## Spesifikasi Aplikasi

Spesifikasi dari aplikasi yang dibangun sebagai berikut

* Aplikasi mampu menerima pesan dari server dengan format JSON berisi lokasi dan token.
* Aplikasi mampu mengolah data berupa location point (longitude, latitude) dan menampilkan indicator pada peta lokasi yang dimaksud. Peta lokasi menggunakan Google Map API.
* Terdapat sebuah panah navigasi yang berada diatas peta, yang menunjukan arah utara.
* Aplikasi mampu mengirim intent kamera.
* Aplikasi mampu mengambil gambar melalui kamera. Gambar tidak diunggah ke server
* Aplikasi mampu mengirimkan pesan ke sever dengan format JSON berisi lokasi (longitude, latitude), nim serta token.
* Android API Level Minimum: 16 (Android JellyBean)
* Ketika orientasi layar portrait, tombol berada pada bawah layar. Ketika landscape, tombol berada pada samping kanan layar 
* Hasil reply dari server harus ditampilkan dalam bentuk *toast*
* Aplikasi menggunakan sensor orientasi. Sensor dilepas ketika aplikasi meninggalkan activity

## Spesifikasi Pertukaran Pesan
### Request Location
Permintaan lokasi (pertama).

**Client Request**
```sh
{“com”:”req_loc”,”nim”:”13512999”}
```
**Server Response** 
```sh
{“status”:”ok”,”nim”:”13512999”,”longitude”:6.234123132,”latitude”:0.1234123412,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
### Send Answer
Mengirimkan jawaban dan menerima lokasi berikutnya

**Client Request**
```sh
{“com”:”answer”,”nim”:”13512999”,”answer”:”labtek_v”, ”longitude”:6.234123132,”latitude”:0.1234123412,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
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

**asumsi** lokasi yang dikirimkan adalah lokasi yang didapat sebelumnya

**Server Response**
Jika jawaban **benar**, maka:
```sh
{“status”:”ok”,”nim”:”13512999”,”longitude”:”8.13215123214”,”latitude”:”9.1234123412”,”token”:”124fewfm32r32ifmwder42”}
```
Jika jawaban **benar dan sudah berada dilokasi ketiga**, maka:
```sh
{“status”:”finish”,”nim”:”13512999”,”token”:”124fewfm32r32ifmwder42”,”check”:1}
```

## Tampilan
**Tampilan input IP address, Port, dan NIM**
![alt text](http://i.imgur.com/iOeYHju.png)

**Tampilan horizontal**
![alt text](http://i.imgur.com/AaaThyN.png)

**Tampilan vertikal**
![alt text](http://i.imgur.com/hHDu4cX.png)

**Tampilan *submit* jawaban**
![alt text](http://i.imgur.com/Vm29rmB.png)

[Markdown]: <http://dillinger.io/>
