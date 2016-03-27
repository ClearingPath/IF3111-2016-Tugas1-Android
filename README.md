# Tugas Besar 1 IF3111 Pengembangan Aplikasi pada Platform Khusus

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

## Lokasi File
Lokasi Source Code berada pada \Tubes1Android\app\src\main
Lokasi Binary berada pada \Tubes1Android\app\build\outputs\apk

##Contoh percakapan Client dan Server
**Client Request**
```sh
{“com”:”req_loc”,”nim”:”13513030”}
```
**Server Response** 
```sh
{“status”:”ok”,”nim”:”13513030”,”longitude”:”6.234123132”,”latitude”:”0.1234123412”,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
### Send Answer
Mengirimkan jawaban dan menerima lokasi berikutnya

**Client Request**
```sh
{“com”:”answer”,”nim”:”13513030”,”answer”:”labtek_v”, ”longitude”:”6.234123132”,”latitude”:”0.1234123412”,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
**Server Response**
Jika jawaban Anda **benar**, maka:
```sh
{“status”:”ok”,”nim”:”13513030”,”longitude”:”8.13215123214”,”latitude”:”9.1234123412”,”token”:”124fewfm32r32ifmwder42”}
```
Jika jawaban Anda **salah**, maka:
```sh
{“status”:”wrong_answer”,”nim”:”13513030”,”token”:”124fewfm32r32ifmwder42”}
```
Jika jawaban Anda **benar dan sudah berada dilokasi ketiga**, maka:
```sh
{“status”:”finish”,”nim”:”13513030”,”token”:”124fewfm32r32ifmwder42”,”check”:1}
```
Terdapat tombol log untuk melihat sejarah percakapan.

## Spesifikasi Tampilan
**Tampilan horizontal**
![alt text](http://imgur.com/VIvr9z7)
**Tampilan vertikal**
![alt text](http://imgur.com/DOYQUy9)
**Tampilan *submit* jawaban**
![alt text](http://imgur.com/igArDGQ)
**Tampilan log**
![alt text](http://imgur.com/C0Ocgfk)
**Tampilan spinner
![alt text](http://imgur.com/Xt1N9Yp)
## Asumsi
*Latitude dan Longitude yang dikirimkan lewat halaman "submit answer" adalah latitude dan longitude lokasi device
*Jika terjadi permasalahan koneksi, ditangani dengan membuka app kembali untuk mendapatkan lokasi terakhir

[Markdown]: <http://dillinger.io/>