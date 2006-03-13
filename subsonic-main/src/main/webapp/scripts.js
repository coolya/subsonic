// $Revision: 1.1 $ $Date: 2005/03/04 20:47:31 $

function popup(mylink, windowname) {
  if (!window.focus) return true;
  var href;
  if (typeof(mylink) == 'string')
     href = mylink;
  else
     href = mylink.href;
  window.open(href, windowname, 'width=400,height=200,scrollbars=yes');
  return false;
}

