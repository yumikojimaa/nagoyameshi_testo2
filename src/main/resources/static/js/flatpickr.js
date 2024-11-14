let maxDate = new Date();
 maxDate = maxDate.setMonth(maxDate.getMonth() + 3);
// 定休日を取得する
const regularHolidayText = document.getElementById("regularHoliday").textContent;
// 定休日をラベルから数値に変換する
const regularHoliday = ['日','月','火','水','木','金','土'].indexOf(regularHolidayText);

 flatpickr('#reservation_date', {
   locale: 'ja',
   minDate: 'today',
   maxDate: maxDate,
   disable     : [
      // カレンダーより選択された日付の曜日が定休日か否か判定する
      (date) => date.getDay() === regularHoliday
   ]
 });