function popup(mylink, windowname) {
  if (!window.focus) return true;
  var href;
  if (typeof(mylink) == 'string')
     href = mylink;
  else
     href = mylink.href;
  window.open(href, windowname, 'width=400,height=200,screenX=400,screenY=300,scrollbars=yes,resizable=yes');
  return false;
}

