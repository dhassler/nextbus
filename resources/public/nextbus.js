function timer(){
 var now     = new Date,
     hours   = now.getHours(),
     ampm    = hours<12 ? ' AM' : ' PM',
     minutes = now.getMinutes(),
     seconds = now.getSeconds(),
     t_str   = [(hours < 10 ? "0" + hours : hours),
                (minutes < 10 ? "0" + minutes : minutes),
                (seconds < 10 ? "0" + seconds : seconds)].join(":");
 document.getElementById('current-time').innerHTML = t_str;
 setTimeout(timer,1000);
}

timer();

function swap(){
  var loc = window.location + "";
  if (loc.endsWith("b")) {
    window.location = loc.replace(/b$/, "l");
  }
  else {
    window.location = loc.replace(/l*$/, "b");
  }
}
