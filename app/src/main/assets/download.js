(function(){
 const https=(v)=>{try{const u=new URL(v,location.href);return u.protocol==='https:'?u.href:null}catch(e){return null}};
 const path=location.pathname.match(/\/(?:video|share\/video)\/(\d+)/);
 const expected=path?path[1]:null;
 let result={url:null,title:document.title||'',id:expected,blocked:false};
 function addr(a){if(typeof a==='string')return https(a);if(!a||typeof a!=='object')return null;const list=a.url_list||a.urlList||[];for(const x of list){const u=https(x);if(u)return u}return null}
 function yes(v){return v===true||v===1||v==='1'}
 function no(v){return v===false||v===0||v==='0'}
 function walk(root){const stack=[root];let count=0;while(stack.length&&count++<50000){const a=stack.pop();if(!a||typeof a!=='object')continue;const id=a.aweme_id||a.awemeId;const video=a.video; if(id&&video&&(!expected||String(id)===expected)){
 const c=a.video_control||a.videoControl||{};const status=a.status||{};
 if(yes(c.prevent_download)||yes(c.preventDownload)||yes(status.is_private)||no(c.allow_download)||no(c.allowDownload)){result.blocked=true;result.url=null;return;}
 const allowed=no(c.prevent_download)||no(c.preventDownload)||yes(c.allow_download)||yes(c.allowDownload)||yes(a.allow_download)||yes(a.allowDownload);
 if(allowed){const u=addr(video.download_addr||video.downloadAddr);if(u){result.url=u;result.title=a.desc||result.title;result.id=String(id);return;}}
 }for(const k of Object.keys(a)){if(a[k]&&typeof a[k]==='object')stack.push(a[k]);}}}
 try{if(window._ROUTER_DATA)walk(window._ROUTER_DATA);}catch(e){}
 if(!result.blocked&&!result.url){for(const el of document.querySelectorAll('script[type="application/json"],script#RENDER_DATA')){try{let t=el.textContent.trim();if(t.startsWith('%'))t=decodeURIComponent(t);walk(JSON.parse(t));if(result.blocked||result.url)break;}catch(e){}}}
 if(!result.blocked&&!result.url){for(const a of document.querySelectorAll('a[download]')){const u=https(a.getAttribute('href'));if(u&&(/\.mp4(?:[?#]|$)/i.test(u)||/\.mp4$/i.test(a.getAttribute('download')||''))){result.url=u;break;}}}
 return result;
})()
