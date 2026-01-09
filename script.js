const apiKey = "319eb791872b393e9a40b2ea08eb2bc0";

// دالة لجلب الطقس باستخدام اسم المدينة (للبحث اليدوي)
async function checkWeatherByCity(city) {
    const response = await fetch(`https://api.openweathermap.org/data/2.5/weather?units=metric&q=${city}&appid=${apiKey}`);
    let data = await response.json();
    updateUI(data);
}

// دالة لجلب الطقس باستخدام الإحداثيات (للموقع التلقائي)
async function checkWeatherByCoords(lat, lon) {
    const response = await fetch(`https://api.openweathermap.org/data/2.5/weather?units=metric&lat=${lat}&lon=${lon}&appid=${apiKey}`);
    let data = await response.json();
    updateUI(data);
}

// دالة لتحديث واجهة المستخدم (حتى لا نكرر الكود)
function updateUI(data) {
    if(data.cod === "404") {
        alert("المدينة غير موجودة!");
        return;
    }
    
    document.querySelector(".city").innerHTML = data.name;
    document.querySelector(".temp").innerHTML = Math.round(data.main.temp) + "°م";
    document.querySelector(".humidity").innerHTML = data.humidity + "%";
    document.querySelector(".wind").innerHTML = data.wind.speed + " كم/س";

    const main = data.weather[0].main;
    const hours = new Date().getHours();
    const isNight = hours > 18 || hours < 6;

    // تغيير الإيموجي والخلفية
    updateTheme(main, isNight);
}

function updateTheme(condition, isNight) {
    const weatherIcon = document.getElementById("weather-icon");
    const icons = {
        "Clouds": "☁️",
        "Clear": isNight ? "🌙" : "☀️",
        "Rain": "🌧️",
        "Drizzle": "🌦️",
        "Mist": "🌫️",
        "Snow": "❄️"
    };

    weatherIcon.innerHTML = icons[condition] || "🌡️";
    document.body.style.background = isNight 
        ? "linear-gradient(135deg, #1a2a6c, #b21f1f, #fdbb2d)" // غروب/ليل
        : "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)"; // نهار مشرق
}

// --- الجزء الخاص بتحديد الموقع التلقائي عند تشغيل الصفحة ---
window.onload = function() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition((position) => {
            const lat = position.coords.latitude;
            const lon = position.coords.longitude;
            checkWeatherByCoords(lat, lon);
        }, () => {
            // في حال رفض المستخدم الإذن، نعرض طقس مدينة افتراضية
            checkWeatherByCity("Riyadh");
        });
    } else {
        checkWeatherByCity("Riyadh");
    }
};

// تفعيل زر البحث اليدوي
document.querySelector(".search button").addEventListener("click", () => {
    checkWeatherByCity(document.querySelector(".search input").value);
});
