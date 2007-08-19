function popup(mylink, windowname) {
    return popupSize(mylink, windowname, 400, 200);
}

function popupSize(mylink, windowname, width, height) {
  if (!window.focus) return true;
  var href;
  if (typeof(mylink) == "string")
     href = mylink;
  else
     href = mylink.href;
  window.open(href, windowname, "width=" + width + ",height=" + height + ",screenX=400,screenY=300,scrollbars=yes,resizable=yes");
  return false;
}

