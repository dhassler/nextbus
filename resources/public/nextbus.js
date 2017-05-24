function timer(){
 var now     = new Date,
     hours   = now.getHours()<12 ? now.getHours() : now.getHours()-12,
     minutes = now.getMinutes(),
     seconds = now.getSeconds(),
     t_str   = [hours,
                (minutes < 10 ? "0" + minutes : minutes),
                (seconds < 10 ? "0" + seconds : seconds)].join(":");
 document.getElementById('current-time').innerHTML = t_str;
 setTimeout(timer,1000);
}

timer();

function swap(){
  var loc = window.location + "";
  if (/.*\/b$/.test(loc)) {
    window.location = loc.replace(/b$/, "l");
  }
  else {
    window.location = loc.replace(/l*$/, "b");
  }
}
