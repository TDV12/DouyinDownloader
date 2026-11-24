const assert=require('node:assert/strict');const fs=require('fs');const vm=require('vm');
const script=fs.readFileSync('app/src/main/assets/download.js','utf8');
const id='1234567890123456789';
function run(item,extra={}){return vm.runInNewContext(script,{URL,location:new URL('https://www.iesdouyin.com/share/video/'+id+'/'),window:{_ROUTER_DATA:{videoInfoRes:{item_list:[item]}},...extra},document:{title:'Test',querySelectorAll:()=>[]}});}
const play='https://example.org/original.mp4';const download='https://example.org/download.mp4';
const v={aweme_id:id,video:{play_addr:{url_list:[play]}}};
assert.equal(run(v).reason,'no_permission');
assert.equal(run({...v,video_control:{allow_download:true}}).url,play);
assert.equal(run({...v,video_control:{allow_download:true,prevent_download_type:1}}).blocked,true);
assert.equal(run({...v,video_control:{allow_download:false}}).url,null);
assert.equal(run({...v,video_control:{allow_download:true},video:{...v.video,download_addr:{url_list:[download]}}}).url,download);
assert.equal(run({...v,aweme_id:'999',video_control:{allow_download:true}}).reason,'no_metadata');
assert.equal(run({...v,video_control:{allow_download:true},video:{}}).reason,'no_address');
assert.equal(run({...v,video_control:{allow_download:true},status:{is_private:true}}).blocked,true);
console.log('8 parser tests passed');
