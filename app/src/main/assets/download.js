(function () {
  const path = location.pathname.match(/\/(?:share\/)?video\/(\d+)/);
  const expected = path ? path[1] : new URL(location.href).searchParams.get('modal_id');
  const r = {url:null,title:document.title||'',id:expected,blocked:false,reason:'no_metadata',source:null};
  const yes = v => v===true || v===1 || v==='1' || v==='true';
  const no = v => v===false || v===0 || v==='0' || v==='false';
  const https = v => {if(typeof v!=='string'||!v)return null;try{const u=new URL(v,location.href);return u.protocol==='https:'&&!u.username&&!u.password?u.href:null;}catch(e){return null;}};
  function addr(a) {
    if(typeof a==='string')return https(a);
    if(!a||typeof a!=='object')return null;
    if(Array.isArray(a)){for(const x of a){const u=addr(x);if(u)return u;}return null;}
    return addr(a.url_list||a.urlList||a.urls||a.url||null);
  }
  const roots=[];
  function parse(s){if(typeof s!=='string')return s;let t=s.trim();try{if(t.startsWith('%'))t=decodeURIComponent(t);return JSON.parse(t);}catch(e){return null;}}
  for(const k of ['_ROUTER_DATA','__INITIAL_STATE__','__NEXT_DATA__','RENDER_DATA']){try{const v=parse(window[k]);if(v)roots.push(v);}catch(e){}}
  for(const e of document.querySelectorAll('script[type="application/json"],script#RENDER_DATA,script#__NEXT_DATA__')){const v=parse(e.textContent);if(v)roots.push(v);}
  const seen=new Set();let found=false, allowed=false, candidate=null, fallback=null, title=r.title;
  const stack=roots.slice();let count=0;
  while(stack.length&&count++<60000){
    const a=stack.pop();if(!a||typeof a!=='object'||seen.has(a))continue;seen.add(a);
    const id=a.aweme_id||a.awemeId||a.item_id||a.itemId;
    const video=a.video;
    // Match the requested video, never a recommendation.
    if(expected&&id&&String(id)===expected&&video&&typeof video==='object'){
      found=true;const c=a.video_control||a.videoControl||{};const s=a.status||{};
      const denied=[c.prevent_download,c.preventDownload,s.is_private,s.isPrivate,a.is_private,a.isPrivate].some(yes)||
        [c.allow_download,c.allowDownload,a.allow_download,a.allowDownload].some(no)||
        [c.prevent_download_type,c.preventDownloadType].some(v=>v!=null&&Number(v)>0);
      if(denied){r.blocked=true;r.reason='denied';return r;}
      const granted=[c.allow_download,c.allowDownload,a.allow_download,a.allowDownload].some(yes);
      if(granted){allowed=true;title=a.desc||a.description||title;
        candidate=candidate||addr(video.download_addr||video.downloadAddr);
        // Only if saving is explicitly allowed. Preserve the original URL and watermark.
        fallback=fallback||addr(video.play_addr||video.playAddr);
      }
    }
    for(const k of Object.keys(a)){const v=a[k];if(v&&typeof v==='object')stack.push(v);}
  }
  if(candidate||fallback){r.url=candidate||fallback;r.source=candidate?'download_addr':'play_addr_allowed';r.title=title;r.reason='ok';return r;}
  // A real download link supplied by the page is also an explicit download option.
  if(expected){for(const a of document.querySelectorAll('a[download]')){const u=https(a.getAttribute('href'));if(u&&(/\.mp4(?:[?#]|$)/i.test(u)||/\.mp4$/i.test(a.getAttribute('download')||''))){r.url=u;r.source='official_link';r.reason='ok';return r;}}}
  r.reason=!expected?'no_id':!found?'no_metadata':!allowed?'no_permission':'no_address';
  return r;
})()
