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

## Spesifikasi Tanya-Jawab oleh Asisten
Pada akhir eksplorasi lokasi oleh peserta, akan ada tanya-jawab dengan asisten. Anda juga diminta memberikan hasil foto yang anda dapat (tidak perlu lewat aplikasi yang dibuat pada tugas ini, dapat melalui gallery bawaan android anda). Asisten akan melakukan cross-check dengan data yang anda kirimkan.


## Spesifikasi Pertukaran Pesan
Keterangan : *Client* pada dokumen ini adalah aplikasi Anda dan *Server* merupakan server milik asisten.
### Request Location
Permintaan lokasi (pertama).

**Client Request**
```sh
{“com”:”req_loc”,”nim”:”13512999”}
```
**Server Response** 
Before :
```sh
{“status”:”ok”,”nim”:”13512999”,”longitude”:”6.234123132”,”latitude”:”0.1234123412”,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
After :
Longitude dan Latitude menggunakan tipe Long.
```sh
{“status”:”ok”,”nim”:”13512999”,”longitude”:6.234123132,”latitude”:0.1234123412,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
### Send Answer
Mengirimkan jawaban dan menerima lokasi berikutnya

**Client Request**
Before :
```sh
{“com”:”answer”,”nim”:”13512999”,”answer”:”labtek_v”, ”longitude”:”6.234123132”,”latitude”:”0.1234123412”,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
After :
Longitude dan Latitude menggunakan tipe Long.
```sh
{“com”:”answer”,”nim”:”13512999”,”answer”:”labtek_v”, ”longitude”:6.234123132,”latitude”:0.1234123412,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
**Server Response**
Jika jawaban Anda **benar**, maka:
```sh
{“status”:”ok”,”nim”:”13512999”,”longitude”:”8.13215123214”,”latitude”:”9.1234123412”,”token”:”124fewfm32r32ifmwder42”}
```
Jika jawaban Anda **salah**, maka:
```sh
{“status”:”wrong_answer”,”nim”:”13512999”,”token”:”124fewfm32r32ifmwder42”}
```
Jika jawaban Anda **benar dan sudah berada dilokasi ketiga**, maka:
```sh
{“status”:”finish”,”nim”:”13512999”,”token”:”124fewfm32r32ifmwder42”,”check”:1}
```

### Extra
**No NIM**
```sh
{“status”:”err”,”nim”:””,”token”:”124fewfm32r32ifmwder42”}
```
**No Com command**
```sh
{“status”:”err”,”nim”:”13512999”,”token”:”124fewfm32r32ifmwder42”}
```
## Spesifikasi Tampilan
**Tampilan horizontal**
![alt text](http://i.imgur.com/Q1ZhAvb.png)
**Tampilan vertikal**
![alt text](http://i.imgur.com/L7RnXCm.png)
**Tampilan *submit* jawaban**
![alt text](http://i.imgur.com/TlaiEs9.png)
## Deliverables

Silahkan ikuti langkah pengumpulan berikut :

- Lakukan **fork** terhadap repository ini.
- Edit file readme ini semenarik mungkin (gunakan panduan [Markdown] langguage), diperbolehkan untuk merubah struktur dari readme ini. (Soal tidak perlu dipertahankan).
- Pada Readme terdapat tampilan aplikasi.
- Cantumkan lokasi *source code* dan *binary* dari aplikasi pada Readme.

## Deadline
Deadline pull request terakhir (termasuk commit) adalah 27 Maret 2016 pukul 23.55. 


## Keterangan Tambahan
Bila ada pertanyaan, mengenai tugas ini silahkan lakukan melalui milis IF3111.

[Markdown]: <http://dillinger.io/>