# Tugas Besar 1 IF3111 Pengembangan Aplikasi pada Platform Khusus

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
* Aplikasi mampu mengolah data berupa location point (longitude, latitude) dan menampilkan indicator pada peta lokasi yang dimaksud. Peta lokasi menggunakan Google Map API. (Tampilan silahkan lihat Spesifikasi Tampilan).
* Terdapat sebuah panah navigasi yang berada diatas peta (letak bebas), yang menunjukan arah utara. Anda diminta menggunakan sensor yang ada pada android API (Tampilan silahkan lihat Spesifikasi Tampilan).
* Aplikasi mampu mengirim intent kamera.
* Aplikasi mampu mengambil gambar melalui kamera. Gambar tidak perlu diunggah ke server (silakan lihat spesifikasi tanya jawab asisten)
* Aplikasi mampu mengirimkan pesan ke sever dengan format JSON berisi lokasi (longitude, latitude), nim serta token.
* Pastikan SDK anda mendukung pengerjaan tugas ini.
* Perhatikan tata letak tombol. Ketika orientasi layar portrait, tombol berada pada bawah layar. Ketika landscape, tombol berada pada samping kanan layar (lihat contoh tampilan seperti pada mock-up spesifikasi tampilan). Anda dapat menggunakan fragment untuk masalah ini.
* Tampilan warna, font, style tidak dinilai. Namun tata letak tombol akan dinilai.
* Hasil reply dari server harus ditampilkan dalam bentuk *toast* atau *alert dialog* (pilih satu).
* Ketika anda menggunakan activity yang memanggil sensor, sensor tersebut harus dilepas ketika anda berpindah activity agar tidak boros baterai.

## Lokasi Source Code : \app\src\main

## Lokasi Binary : \APK

## Spesifikasi Tampilan
**Tampilan horizontal**
![alt text](http://imgur.com/zMCx3fy.png)
**Tampilan vertikal**
![alt text](http://imgur.com/zd9yqG2.png)
**Tampilan *submit* jawaban**
![alt text](http://imgur.com/GYox6t1.png)
