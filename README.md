# ITB Locator

ITB Locator adalah game mobile yang mudah, ringan, dan menyenangkan. Dengan game ini kalian dapat mengetes pengetahuan kalian tentang tempat-tempat di ITB.

Ada beberapa fitur yang akan kalian dapatkan di game ini :
  - Pertanyaan berupa lokasi dalam Google Maps
  - Ada juga kompas yang memudahkan kalian mencari mata angin jika belum kenal tempat-tempat di ITB
  - GPS sensor yang menunjukkan lokasi kalian
  - Fitur **capture** untuk mengambil gambar dari tempat yang ditanyakan agar selalu ingat
  - Hasil jawaban yang dapat ditampilkan seketika dengan notifikasi **toast**

Untuk saat ini game hanya dapat dimainkan di smartphone Android. Untuk versi iOS dan Windows Phone akan segera menyusul.

![PlayStore](https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png)

### Tampilan
![GitHub Logo](/images/main.png)
Tampilan saat portrait
![GitHub Logo](/images/main_land.png)
Tampilan saat landscape
![GitHub Logo](/images/ans.png)
Laman untuk menjawab
![GitHub Logo](/images/wrong.png)
Ups! Jawaban salah

### Struktur Direktori
- *app/src/main/java/org/informatika/gitlab/icalf/itblocator* : kode sumber
- *app/build* : hasil kompilasi
- *app/src/main/res* : aset dan layout

### Log Pertukaran Pesan
Diakibatkan kurang menahunya pembuat saya sebagai pembuat aplikasi atas ketiga tempat yang sama, maka hanya ada log respon pertanyaan kedua dan ketiga.

Request awal
```json
{"com":"req_loc","nim":"13513004"}
```

Respon pertanyaan kedua
```json
{"nim":"13513004","latitude":107.610359,"status":"ok","longitude":-6.890356,"token":"0b50d5f1d422dfb6d44d754cba947acf"}
```

Jawaban untuk pertanyaan ketiga
```json
{"com":"answer","nim":"13513004","latitude":107.610359,"answer":"intel","longitude":-6.890356,"token":"0b50d5f1d422dfb6d44d754cba947acf"}
```

Respon pertanyaan ketiga
```json
{"nim":"13513004","check":1,"status":"finish","token":"d8df89f8a7116da09d8195a24c4e20c5"}
```

### Third-party
Beberapa kode disadur dari [tutorial Google Maps API](https://github.com/googlemaps/android-samples/tree/master/ApiDemos/) yang disediakan di repo milik Google.

### Todos
 - Mengubah operasi komunikasi dengan server dalam *Async Task*
 - Menambahkan detil peta dalam aplikasi seperti *street view* dan lainnya

License
----

2016 ISC license
@icalF

**Fully open source**