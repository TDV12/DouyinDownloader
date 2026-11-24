const assert=require('node:assert/strict');const fs=require('fs');const vm=require('vm');
const script=fs.readFileSync('app/src/main/assets/monitor.js','utf8');
const video={paused:false,ended:false,currentSrc:'blob:https://example.org/id',querySelectorAll:()=>[]};
const urls=['https://example.org/a.mp4?secret=123','https://example.org/a.m3u8','https://example.org/a.m4s','https://v1.douyinvod.com/resource','https://example.org/page?x=.mp4'];
const result=vm.runInNewContext(script,{URL,location:new URL('https://example.org'),document:{querySelectorAll:()=>[video]},performance:{getEntriesByType:()=>[...urls,urls[0]].map(name=>({name}))}});
assert.deepEqual(JSON.parse(JSON.stringify(result)),{videos:1,playing:1,blob:1,mp4:1,hls:1,segments:1,cdn:1});
assert(!JSON.stringify(result).includes('secret'));
console.log('2 monitor tests passed');
