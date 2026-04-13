# EffiTrack Mobile Client
The developed Android application is a comprehensive mobile client for the digitalization of manufacturing processes and equipment efficiency monitoring within the EffiTrack ecosystem. The app architecture follows the MVVM pattern, ensuring a clean separation of UI and business logic, while securely communicating with the backend using JWT authorization to protect corporate data.

The application provides an intuitive interface for the full equipment management lifecycle right from the factory floor. It enables operators to monitor assigned machines and switch real-time statuses (Running, Idle, Setup) with a single tap, contributing to detailed event logging. Functionality includes a fast and reliable QR/Barcode scanning module using the device camera for instant equipment search and assignment, alongside a manual entry fallback for edge cases.

The Task Management module allows operators to view their daily plans, transition tasks through functional states (In Progress, Done), log actual time spent, and leave contextual comments for supervisors. The app also features a one-click shift reporting mechanism that triggers the backend to aggregate daily data, generate performance charts, and email comprehensive reports to managers.

The technology stack includes Kotlin, Jetpack Compose, Coroutines, Retrofit, and Google ML Kit (CameraX).

---

Розроблений Android-додаток є комплексним мобільним клієнтом для цифровізації виробничих процесів та моніторингу ефективності обладнання в рамках екосистеми EffiTrack. Архітектура додатку побудована за патерном MVVM, що забезпечує чіткий поділ між інтерфейсом (UI) та бізнес-логікою, а також безпечну взаємодію з бекендом за допомогою JWT-авторизації для захисту корпоративних даних.

Додаток надає інтуїтивно зрозумілий інтерфейс для повного циклу управління обладнанням безпосередньо з виробничого цеху. Він дозволяє операторам моніторити закріплені верстати та в один клік змінювати їхні статуси в реальному часі (Робота, Простій, Наладка), формуючи детальну історію подій. Функціонал включає швидкий та надійний модуль сканування QR/штрихкодів за допомогою камери пристрою для миттєвого пошуку та закріплення обладнання, а також резервний механізм ручного введення.

Модуль Task Management дозволяє операторам переглядати свій план робіт на день, змінювати стан виконання завдань (В процесі, Виконано), фіксувати фактично витрачений час та залишати коментарі для майстра. Додаток також містить механізм звітування за зміну в один клік, який ініціює на стороні сервера агрегацію даних, генерацію графіків продуктивності та надсилання розгорнутих звітів керівникам електронною поштою.

Технологічний стек включає Kotlin, Jetpack Compose, Coroutines, Retrofit та Google ML Kit (CameraX).
