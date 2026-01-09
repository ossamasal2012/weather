const apiKey = "319eb791872b393e9a40b2ea08eb2bc0";
const apiUrl = "https://api.openweathermap.org/data/2.5/weather?units=metric&q=";
const weatherIcons = { 
    'Clear': '☀️', 'Clouds': '☁️', 'Rain': '🌧️', 
    'Drizzle': '🌦️', 'Thunderstorm': '⛈️', 'Snow': '❄️', 'Mist': '🌫️' 
};

window.onload = () => {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(p => getWeatherData(p.coords.latitude, p.coords.longitude, true), 
        () => getWeatherData('Baghdad'));
    }
};

async function getWeatherData(q, lon = null, isCoords = false) {
    let url = isCoords 
        ? `https://api.openweathermap.org/data/2.5/forecast?lat=${q}&lon=${lon}&appid=${apiKey}&units=metric&lang=ar`
        : `https://api.openweathermap.org/data/2.5/forecast?q=${q}&appid=${apiKey}&units=metric&lang=ar`;

    try {
        const res = await fetch(url);
        const data = await res.json();
        if(data.cod === "200") {
            updateUI(data);
        } else {
            console.error("خطأ من API:", data.message);
        }
    } catch (e) {
        console.error("فشل الاتصال بالخادم");
    }
}

function updateUI(data) {
    // 1. تحديث الطقس الحالي
    const current = data.list[0];
    document.getElementById('cityName').innerText = data.city.name;
    document.getElementById('temp').innerText = `${Math.round(current.main.temp)}°`;
    document.getElementById('description').innerText = current.weather[0].description;
    document.getElementById('humidity').innerText = `${current.main.humidity}%`;
    document.getElementById('windSpeed').innerText = `${current.wind.speed} كم/س`;
    document.getElementById('weatherEmoji').innerText = weatherIcons[current.weather[0].main] || '🌡️';
    document.getElementById('currentDate').innerText = new Date().toLocaleDateString('ar-EG', {weekday: 'long', day: 'numeric', month: 'long'});

    // 2. تحديث توقعات 5 أيام (إصلاح الفراغ)
// --- تحديث توقعات 5 أيام (مع الحرارة العليا والسفلى) ---
const dGrid = document.getElementById('dailyGrid');
dGrid.innerHTML = '';

// تجميع البيانات حسب اليوم
const daysData = {};

data.list.forEach(item => {
    const date = new Date(item.dt * 1000).toLocaleDateString('en-GB'); // مفتاح التاريخ
    if (!daysData[date]) {
        daysData[date] = {
            temps: [],
            icon: item.weather[0].main,
            dayName: new Date(item.dt * 1000).toLocaleDateString('ar-EG', {weekday: 'short'})
        };
    }
    daysData[date].temps.push(item.main.temp);
});

// عرض أول 5 أيام بعد استخراج القيم
Object.values(daysData).slice(0, 5).forEach(day => {
    const maxTemp = Math.round(Math.max(...day.temps)); // أعلى درجة
    const minTemp = Math.round(Math.min(...day.temps)); // أقل درجة

    dGrid.innerHTML += `
        <div class="day-card">
            <p style="font-size:14px; opacity:0.8">${day.dayName}</p>
            <p style="font-size:35px; margin:10px 0">${icons[day.icon] || '☀️'}</p>
            <div style="display: flex; justify-content: center; gap: 8px;">
                <span style="font-weight: bold; color: #ff4d4d;">${maxTemp}°</span> 
                <span style="font-weight: bold; color: #38bdf8;">${minTemp}°</span>
            </div>
        </div>`;
});

// أزرار البحث والحذف والوضع
document.getElementById('searchBtn').onclick = () => {
    const val = document.getElementById('cityInput').value.trim();
    if(val) {
        getWeatherData(val);
        const div = document.createElement('div');
        div.className = 'city-card';
        div.innerHTML = `<span style="cursor:pointer" onclick="getWeatherData('${val}')">${val}</span>
                         <button style="color:red; border:none; background:none; cursor:pointer" onclick="this.parentElement.remove()">✕</button>`;
        document.getElementById('savedCities').appendChild(div);
        document.getElementById('cityInput').value = '';
    }
};

document.getElementById('themeToggle').onclick = () => {
    document.body.classList.toggle('light-mode');
};
