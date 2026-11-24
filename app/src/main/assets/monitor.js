(function(){
  const r={videos:0,playing:0,blob:0,mp4:0,hls:0,segments:0,cdn:0};
  const seen=new Set();
  function observe(value){
    if(typeof value!=='string'||!value||seen.has(value))return;
    seen.add(value);
    try {
      const u=new URL(value,location.href),p=u.pathname.toLowerCase(),h=u.hostname.toLowerCase();
      if(u.protocol==='blob:'){r.blob++;return;}
      if(u.protocol!=='https:')return;
      if(p.endsWith('.mp4'))r.mp4++;
      else if(p.endsWith('.m3u8'))r.hls++;
      else if(p.endsWith('.m4s')||p.endsWith('.ts'))r.segments++;
      else if(h==='douyinvod.com'||h.endsWith('.douyinvod.com')||h.endsWith('.bytecdn.cn')||h.endsWith('.bytevcloudcdn.com'))r.cdn++;
    }catch(e){}
  }
  for(const v of document.querySelectorAll('video')){
    r.videos++;if(!v.paused&&!v.ended)r.playing++;
    observe(v.currentSrc||v.src);
    for(const s of v.querySelectorAll('source'))observe(s.src);
  }
  try{for(const e of performance.getEntriesByType('resource'))observe(e.name);}catch(e){}
  return r;
})()
