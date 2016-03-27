# Tugas Besar 1 IF3111 Pengembangan Aplikasi pada Platform Khusus

## Spesifikasi Aplikasi

Spesifikasi dari aplikasi yang dibangun sebagai berikut

* Aplikasi mampu menerima pesan dari server dengan format JSON berisi lokasi dan token.
* Aplikasi mampu mengolah data berupa location point (longitude, latitude) dan menampilkan indicator pada peta lokasi yang dimaksud. Peta lokasi menggunakan Google Map API. (Tampilan silahkan lihat Screenshot Tampilan).
* Terdapat sebuah panah navigasi yang berada diatas peta (letak bebas), yang menunjukan arah utara. Fitur ini menggunakan sensor yang ada pada android API (Tampilan silahkan lihat Screenshot Tampilan).
* Aplikasi mampu mengirim intent kamera.
* Aplikasi mampu mengambil gambar melalui kamera dan menyimpan gambar tersebut kedalam suatu folder dalam device
* Aplikasi mampu mengirimkan pesan ke sever dengan format JSON berisi lokasi (longitude, latitude), nim serta token.
* Tata letak tombol ketika orientasi layar portrait, tombol berada pada bawah layar. Ketika landscape, tombol berada pada samping kanan layar (lihat contoh tampilan seperti pada mock-up spesifikasi tampilan). Anda dapat menggunakan fragment untuk masalah ini.
* Hasil reply dari server harus ditampilkan dalam bentuk toast.
* Saat menggunakan activity yang memanggil sensor, sensor tersebut harus dilepas ketika anda berpindah activity agar tidak boros baterai.
* Aplikasi dapat menampilkan sejarah percapakan antara server dengan client

## Lokasi File
* Lokasi Source Code berada pada \Tubes1Android\app\src\main.
* Lokasi Binary berada pada \Tubes1Android\app\build\outputs\apk.

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

## Screenshot Tampilan
**Tampilan horizontal**
![alt text](/Screenshots/portrait.png)
**Tampilan vertikal**
![alt text](/Screenshots/landscape.png)
**Tampilan *submit* jawaban**
![alt text](/Screenshots/submit.png)
**Tampilan log**
![alt text](/Screenshots/log.png)
**Tampilan spinner
![alt text](/Screenshots/spinner.png)
## Asumsi
* Latitude dan Longitude yang dikirimkan lewat halaman "submit answer" adalah latitude dan longitude lokasi device.
* Jika terjadi permasalahan koneksi, ditangani dengan membuka app kembali untuk mendapatkan lokasi terakhir.

[Markdown]: <http://dillinger.io/>