import{d as ee,k as _,ao as Eo,V as we,n as Re,h as C,r as Oo,A as Ro,ap as Fo,aq as _o,ae as I,b as T,q as M,K as z,s as G,m as L,e as Ao,f as Q,l as Bo,t as J,x as Mo,g as K,j as Fe,Z as No,a as Ko,z as B,ar as Lo,w as ge,v as Wo,F as Ho,Y as Z}from"./index.ce767858.js";import{W as Do}from"./Scrollbar.f69b01b3.js";import{a as Ie,c as Go}from"./resolve-slot.12a1cf6a.js";import{c as ze}from"./use-form-item.42764c26.js";import{c as jo,a as Uo}from"./cssr.adc11eb3.js";import{N as Vo}from"./Popover.691bc0ba.js";import{g as qo}from"./Empty.de2c8a63.js";import{N as Yo}from"./Suffix.787adf2b.js";import{r as $e}from"./VResizeObserver.520abcae.js";function Kn(e,n){let{target:o}=e;for(;o;){if(o.dataset&&o.dataset[n]!==void 0)return!0;o=o.parentElement}return!1}const H="v-hidden",Zo=Uo("[v-hidden]",{display:"none!important"}),Te=ee({name:"Overflow",props:{getCounter:Function,getTail:Function,updateCounter:Function,onUpdateOverflow:Function},setup(e,{slots:n}){const o=_(null),r=_(null);function i(){const{value:l}=o,{getCounter:t,getTail:f}=e;let s;if(t!==void 0?s=t():s=r.value,!l||!s)return;s.hasAttribute(H)&&s.removeAttribute(H);const{children:g}=l,c=l.offsetWidth,m=[],p=n.tail?f==null?void 0:f():null;let v=p?p.offsetWidth:0,S=!1;const k=l.children.length-(n.tail?1:0);for(let w=0;w<k-1;++w){if(w<0)continue;const $=g[w];if(S){$.hasAttribute(H)||$.setAttribute(H,"");continue}else $.hasAttribute(H)&&$.removeAttribute(H);const u=$.offsetWidth;if(v+=u,m[w]=u,v>c){const{updateCounter:b}=e;for(let x=w;x>=0;--x){const O=k-1-x;b!==void 0?b(O):s.textContent=`${O}`;const E=s.offsetWidth;if(v-=m[x],v+E<=c||x===0){S=!0,w=x-1,p&&(w===-1?(p.style.maxWidth=`${c-E}px`,p.style.boxSizing="border-box"):p.style.maxWidth="");break}}}}const{onUpdateOverflow:y}=e;S?y!==void 0&&y(!0):(y!==void 0&&y(!1),s.setAttribute(H,""))}const d=Eo();return Zo.mount({id:"vueuc/overflow",head:!0,anchorMetaName:jo,ssr:d}),we(i),{selfRef:o,counterRef:r,sync:i}},render(){const{$slots:e}=this;return Re(this.sync),C("div",{class:"v-overflow",ref:"selfRef"},[Oo(e,"default"),e.counter?e.counter():C("span",{style:{display:"inline-block"},ref:"counterRef"}),e.tail?e.tail():null])}});function Jo(e,n){n&&(we(()=>{const{value:o}=e;o&&$e.registerHandler(o,n)}),Ro(()=>{const{value:o}=e;o&&$e.unregisterHandler(o)}))}const Ln=ee({props:{onFocus:Function,onBlur:Function},setup(e){return()=>C("div",{style:"width: 0; height: 0",tabindex:0,onFocus:e.onFocus,onBlur:e.onBlur})}});function Ee(e){return Array.isArray(e)?e:[e]}const xe={STOP:"STOP"};function _e(e,n){const o=n(e);e.children!==void 0&&o!==xe.STOP&&e.children.forEach(r=>_e(r,n))}function Qo(e,n={}){const{preserveGroup:o=!1}=n,r=[],i=o?l=>{l.isLeaf||(r.push(l.key),d(l.children))}:l=>{l.isLeaf||(l.isGroup||r.push(l.key),d(l.children))};function d(l){l.forEach(i)}return d(e),r}function Xo(e,n){const{isLeaf:o}=e;return o!==void 0?o:!n(e)}function en(e){return e.children}function on(e){return e.key}function nn(){return!1}function tn(e,n){const{isLeaf:o}=e;return!(o===!1&&!Array.isArray(n(e)))}function rn(e){return e.disabled===!0}function ln(e,n){return e.isLeaf===!1&&!Array.isArray(n(e))}function pe(e){var n;return e==null?[]:Array.isArray(e)?e:(n=e.checkedKeys)!==null&&n!==void 0?n:[]}function Ce(e){var n;return e==null||Array.isArray(e)?[]:(n=e.indeterminateKeys)!==null&&n!==void 0?n:[]}function an(e,n){const o=new Set(e);return n.forEach(r=>{o.has(r)||o.add(r)}),Array.from(o)}function sn(e,n){const o=new Set(e);return n.forEach(r=>{o.has(r)&&o.delete(r)}),Array.from(o)}function cn(e){return(e==null?void 0:e.type)==="group"}function Wn(e){const n=new Map;return e.forEach((o,r)=>{n.set(o.key,r)}),o=>{var r;return(r=n.get(o))!==null&&r!==void 0?r:null}}class dn extends Error{constructor(){super(),this.message="SubtreeNotLoadedError: checking a subtree whose required nodes are not fully loaded."}}function un(e,n,o,r){return X(n.concat(e),o,r,!1)}function hn(e,n){const o=new Set;return e.forEach(r=>{const i=n.treeNodeMap.get(r);if(i!==void 0){let d=i.parent;for(;d!==null&&!(d.disabled||o.has(d.key));)o.add(d.key),d=d.parent}}),o}function fn(e,n,o,r){const i=X(n,o,r,!1),d=X(e,o,r,!0),l=hn(e,o),t=[];return i.forEach(f=>{(d.has(f)||l.has(f))&&t.push(f)}),t.forEach(f=>i.delete(f)),i}function me(e,n){const{checkedKeys:o,keysToCheck:r,keysToUncheck:i,indeterminateKeys:d,cascade:l,leafOnly:t,checkStrategy:f,allowNotLoaded:s}=e;if(!l)return r!==void 0?{checkedKeys:an(o,r),indeterminateKeys:Array.from(d)}:i!==void 0?{checkedKeys:sn(o,i),indeterminateKeys:Array.from(d)}:{checkedKeys:Array.from(o),indeterminateKeys:Array.from(d)};const{levelTreeNodeMap:g}=n;let c;i!==void 0?c=fn(i,o,n,s):r!==void 0?c=un(r,o,n,s):c=X(o,n,s,!1);const m=f==="parent",p=f==="child"||t,v=c,S=new Set,k=Math.max.apply(null,Array.from(g.keys()));for(let y=k;y>=0;y-=1){const w=y===0,$=g.get(y);for(const u of $){if(u.isLeaf)continue;const{key:b,shallowLoaded:x}=u;if(p&&x&&u.children.forEach(P=>{!P.disabled&&!P.isLeaf&&P.shallowLoaded&&v.has(P.key)&&v.delete(P.key)}),u.disabled||!x)continue;let O=!0,E=!1,A=!0;for(const P of u.children){const N=P.key;if(!P.disabled){if(A&&(A=!1),v.has(N))E=!0;else if(S.has(N)){E=!0,O=!1;break}else if(O=!1,E)break}}O&&!A?(m&&u.children.forEach(P=>{!P.disabled&&v.has(P.key)&&v.delete(P.key)}),v.add(b)):E&&S.add(b),w&&p&&v.has(b)&&v.delete(b)}}return{checkedKeys:Array.from(v),indeterminateKeys:Array.from(S)}}function X(e,n,o,r){const{treeNodeMap:i,getChildren:d}=n,l=new Set,t=new Set(e);return e.forEach(f=>{const s=i.get(f);s!==void 0&&_e(s,g=>{if(g.disabled)return xe.STOP;const{key:c}=g;if(!l.has(c)&&(l.add(c),t.add(c),ln(g.rawNode,d))){if(r)return xe.STOP;if(!o)throw new dn}})}),t}function bn(e,{includeGroup:n=!1,includeSelf:o=!0},r){var i;const d=r.treeNodeMap;let l=e==null?null:(i=d.get(e))!==null&&i!==void 0?i:null;const t={keyPath:[],treeNodePath:[],treeNode:l};if(l!=null&&l.ignored)return t.treeNode=null,t;for(;l;)!l.ignored&&(n||!l.isGroup)&&t.treeNodePath.push(l),l=l.parent;return t.treeNodePath.reverse(),o||t.treeNodePath.pop(),t.keyPath=t.treeNodePath.map(f=>f.key),t}function vn(e){if(e.length===0)return null;const n=e[0];return n.isGroup||n.ignored||n.disabled?n.getNext():n}function gn(e,n){const o=e.siblings,r=o.length,{index:i}=e;return n?o[(i+1)%r]:i===o.length-1?null:o[i+1]}function Oe(e,n,{loop:o=!1,includeDisabled:r=!1}={}){const i=n==="prev"?pn:gn,d={reverse:n==="prev"};let l=!1,t=null;function f(s){if(s!==null){if(s===e){if(!l)l=!0;else if(!e.disabled&&!e.isGroup){t=e;return}}else if((!s.disabled||r)&&!s.ignored&&!s.isGroup){t=s;return}if(s.isGroup){const g=ke(s,d);g!==null?t=g:f(i(s,o))}else{const g=i(s,!1);if(g!==null)f(g);else{const c=Cn(s);c!=null&&c.isGroup?f(i(c,o)):o&&f(i(s,!0))}}}}return f(e),t}function pn(e,n){const o=e.siblings,r=o.length,{index:i}=e;return n?o[(i-1+r)%r]:i===0?null:o[i-1]}function Cn(e){return e.parent}function ke(e,n={}){const{reverse:o=!1}=n,{children:r}=e;if(r){const{length:i}=r,d=o?i-1:0,l=o?-1:i,t=o?-1:1;for(let f=d;f!==l;f+=t){const s=r[f];if(!s.disabled&&!s.ignored)if(s.isGroup){const g=ke(s,n);if(g!==null)return g}else return s}}return null}const mn={getChild(){return this.ignored?null:ke(this)},getParent(){const{parent:e}=this;return e!=null&&e.isGroup?e.getParent():e},getNext(e={}){return Oe(this,"next",e)},getPrev(e={}){return Oe(this,"prev",e)}};function yn(e,n){const o=n?new Set(n):void 0,r=[];function i(d){d.forEach(l=>{r.push(l),!(l.isLeaf||!l.children||l.ignored)&&(l.isGroup||o===void 0||o.has(l.key))&&i(l.children)})}return i(e),r}function xn(e,n){const o=e.key;for(;n;){if(n.key===o)return!0;n=n.parent}return!1}function Ae(e,n,o,r,i,d=null,l=0){const t=[];return e.forEach((f,s)=>{var g;const c=Object.create(r);if(c.rawNode=f,c.siblings=t,c.level=l,c.index=s,c.isFirstChild=s===0,c.isLastChild=s+1===e.length,c.parent=d,!c.ignored){const m=i(f);Array.isArray(m)&&(c.children=Ae(m,n,o,r,i,c,l+1))}t.push(c),n.set(c.key,c),o.has(l)||o.set(l,[]),(g=o.get(l))===null||g===void 0||g.push(c)}),t}function Hn(e,n={}){var o;const r=new Map,i=new Map,{getDisabled:d=rn,getIgnored:l=nn,getIsGroup:t=cn,getKey:f=on}=n,s=(o=n.getChildren)!==null&&o!==void 0?o:en,g=n.ignoreEmptyChildren?u=>{const b=s(u);return Array.isArray(b)?b.length?b:null:b}:s,c=Object.assign({get key(){return f(this.rawNode)},get disabled(){return d(this.rawNode)},get isGroup(){return t(this.rawNode)},get isLeaf(){return Xo(this.rawNode,g)},get shallowLoaded(){return tn(this.rawNode,g)},get ignored(){return l(this.rawNode)},contains(u){return xn(this,u)}},mn),m=Ae(e,r,i,c,g);function p(u){if(u==null)return null;const b=r.get(u);return b&&!b.isGroup&&!b.ignored?b:null}function v(u){if(u==null)return null;const b=r.get(u);return b&&!b.ignored?b:null}function S(u,b){const x=v(u);return x?x.getPrev(b):null}function k(u,b){const x=v(u);return x?x.getNext(b):null}function y(u){const b=v(u);return b?b.getParent():null}function w(u){const b=v(u);return b?b.getChild():null}const $={treeNodes:m,treeNodeMap:r,levelTreeNodeMap:i,maxLevel:Math.max(...i.keys()),getChildren:g,getFlattenedNodes(u){return yn(m,u)},getNode:p,getPrev:S,getNext:k,getParent:y,getChild:w,getFirstAvailableNode(){return vn(m)},getPath(u,b={}){return bn(u,b,$)},getCheckedKeys(u,b={}){const{cascade:x=!0,leafOnly:O=!1,checkStrategy:E="all",allowNotLoaded:A=!1}=b;return me({checkedKeys:pe(u),indeterminateKeys:Ce(u),cascade:x,leafOnly:O,checkStrategy:E,allowNotLoaded:A},$)},check(u,b,x={}){const{cascade:O=!0,leafOnly:E=!1,checkStrategy:A="all",allowNotLoaded:P=!1}=x;return me({checkedKeys:pe(b),indeterminateKeys:Ce(b),keysToCheck:u==null?[]:Ee(u),cascade:O,leafOnly:E,checkStrategy:A,allowNotLoaded:P},$)},uncheck(u,b,x={}){const{cascade:O=!0,leafOnly:E=!1,checkStrategy:A="all",allowNotLoaded:P=!1}=x;return me({checkedKeys:pe(b),indeterminateKeys:Ce(b),keysToUncheck:u==null?[]:Ee(u),cascade:O,leafOnly:E,checkStrategy:A,allowNotLoaded:P},$)},getNonLeafKeys(u={}){return Qo(m,u)}};return $}const wn=e=>{const{textColor2:n,primaryColorHover:o,primaryColorPressed:r,primaryColor:i,infoColor:d,successColor:l,warningColor:t,errorColor:f,baseColor:s,borderColor:g,opacityDisabled:c,tagColor:m,closeIconColor:p,closeIconColorHover:v,closeIconColorPressed:S,borderRadiusSmall:k,fontSizeMini:y,fontSizeTiny:w,fontSizeSmall:$,fontSizeMedium:u,heightMini:b,heightTiny:x,heightSmall:O,heightMedium:E,closeColorHover:A,closeColorPressed:P,buttonColor2Hover:N,buttonColor2Pressed:F,fontWeightStrong:j}=e;return Object.assign(Object.assign({},_o),{closeBorderRadius:k,heightTiny:b,heightSmall:x,heightMedium:O,heightLarge:E,borderRadius:k,opacityDisabled:c,fontSizeTiny:y,fontSizeSmall:w,fontSizeMedium:$,fontSizeLarge:u,fontWeightStrong:j,textColorCheckable:n,textColorHoverCheckable:n,textColorPressedCheckable:n,textColorChecked:s,colorCheckable:"#0000",colorHoverCheckable:N,colorPressedCheckable:F,colorChecked:i,colorCheckedHover:o,colorCheckedPressed:r,border:`1px solid ${g}`,textColor:n,color:m,colorBordered:"rgb(250, 250, 252)",closeIconColor:p,closeIconColorHover:v,closeIconColorPressed:S,closeColorHover:A,closeColorPressed:P,borderPrimary:`1px solid ${I(i,{alpha:.3})}`,textColorPrimary:i,colorPrimary:I(i,{alpha:.12}),colorBorderedPrimary:I(i,{alpha:.1}),closeIconColorPrimary:i,closeIconColorHoverPrimary:i,closeIconColorPressedPrimary:i,closeColorHoverPrimary:I(i,{alpha:.12}),closeColorPressedPrimary:I(i,{alpha:.18}),borderInfo:`1px solid ${I(d,{alpha:.3})}`,textColorInfo:d,colorInfo:I(d,{alpha:.12}),colorBorderedInfo:I(d,{alpha:.1}),closeIconColorInfo:d,closeIconColorHoverInfo:d,closeIconColorPressedInfo:d,closeColorHoverInfo:I(d,{alpha:.12}),closeColorPressedInfo:I(d,{alpha:.18}),borderSuccess:`1px solid ${I(l,{alpha:.3})}`,textColorSuccess:l,colorSuccess:I(l,{alpha:.12}),colorBorderedSuccess:I(l,{alpha:.1}),closeIconColorSuccess:l,closeIconColorHoverSuccess:l,closeIconColorPressedSuccess:l,closeColorHoverSuccess:I(l,{alpha:.12}),closeColorPressedSuccess:I(l,{alpha:.18}),borderWarning:`1px solid ${I(t,{alpha:.35})}`,textColorWarning:t,colorWarning:I(t,{alpha:.15}),colorBorderedWarning:I(t,{alpha:.12}),closeIconColorWarning:t,closeIconColorHoverWarning:t,closeIconColorPressedWarning:t,closeColorHoverWarning:I(t,{alpha:.12}),closeColorPressedWarning:I(t,{alpha:.18}),borderError:`1px solid ${I(f,{alpha:.23})}`,textColorError:f,colorError:I(f,{alpha:.1}),colorBorderedError:I(f,{alpha:.08}),closeIconColorError:f,closeIconColorHoverError:f,closeIconColorPressedError:f,closeColorHoverError:I(f,{alpha:.12}),closeColorPressedError:I(f,{alpha:.18})})},kn={name:"Tag",common:Fo,self:wn},Pn=kn,Sn={color:Object,type:{type:String,default:"default"},round:Boolean,size:{type:String,default:"medium"},closable:Boolean,disabled:{type:Boolean,default:void 0}},In=T("tag",`
 white-space: nowrap;
 position: relative;
 box-sizing: border-box;
 cursor: default;
 display: inline-flex;
 align-items: center;
 flex-wrap: nowrap;
 padding: var(--n-padding);
 border-radius: var(--n-border-radius);
 color: var(--n-text-color);
 background-color: var(--n-color);
 transition: 
 border-color .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 opacity .3s var(--n-bezier);
 line-height: 1;
 height: var(--n-height);
 font-size: var(--n-font-size);
`,[M("strong",`
 font-weight: var(--n-font-weight-strong);
 `),z("border",`
 pointer-events: none;
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 border-radius: inherit;
 border: var(--n-border);
 transition: border-color .3s var(--n-bezier);
 `),z("icon",`
 display: flex;
 margin: 0 4px 0 0;
 color: var(--n-text-color);
 transition: color .3s var(--n-bezier);
 font-size: var(--n-avatar-size-override);
 `),z("avatar",`
 display: flex;
 margin: 0 6px 0 0;
 `),z("close",`
 margin: var(--n-close-margin);
 transition:
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier);
 `),M("round",`
 padding: 0 calc(var(--n-height) / 3);
 border-radius: calc(var(--n-height) / 2);
 `,[z("icon",`
 margin: 0 4px 0 calc((var(--n-height) - 8px) / -2);
 `),z("avatar",`
 margin: 0 6px 0 calc((var(--n-height) - 8px) / -2);
 `),M("closable",`
 padding: 0 calc(var(--n-height) / 4) 0 calc(var(--n-height) / 3);
 `)]),M("icon, avatar",[M("round",`
 padding: 0 calc(var(--n-height) / 3) 0 calc(var(--n-height) / 2);
 `)]),M("disabled",`
 cursor: not-allowed !important;
 opacity: var(--n-opacity-disabled);
 `),M("checkable",`
 cursor: pointer;
 box-shadow: none;
 color: var(--n-text-color-checkable);
 background-color: var(--n-color-checkable);
 `,[G("disabled",[L("&:hover","background-color: var(--n-color-hover-checkable);",[G("checked","color: var(--n-text-color-hover-checkable);")]),L("&:active","background-color: var(--n-color-pressed-checkable);",[G("checked","color: var(--n-text-color-pressed-checkable);")])]),M("checked",`
 color: var(--n-text-color-checked);
 background-color: var(--n-color-checked);
 `,[G("disabled",[L("&:hover","background-color: var(--n-color-checked-hover);"),L("&:active","background-color: var(--n-color-checked-pressed);")])])])]),zn=Object.assign(Object.assign(Object.assign({},Q.props),Sn),{bordered:{type:Boolean,default:void 0},checked:Boolean,checkable:Boolean,strong:Boolean,triggerClickOnClose:Boolean,onClose:[Array,Function],onMouseenter:Function,onMouseleave:Function,"onUpdate:checked":Function,onUpdateChecked:Function,internalCloseFocusable:{type:Boolean,default:!0},internalCloseIsButtonTag:{type:Boolean,default:!0},onCheckedChange:Function}),$n=Ko("n-tag"),ye=ee({name:"Tag",props:zn,setup(e){const n=_(null),{mergedBorderedRef:o,mergedClsPrefixRef:r,inlineThemeDisabled:i,mergedRtlRef:d}=Ao(e),l=Q("Tag","-tag",In,Pn,e,r);Bo($n,{roundRef:J(e,"round")});function t(p){if(!e.disabled&&e.checkable){const{checked:v,onCheckedChange:S,onUpdateChecked:k,"onUpdate:checked":y}=e;k&&k(!v),y&&y(!v),S&&S(!v)}}function f(p){if(e.triggerClickOnClose||p.stopPropagation(),!e.disabled){const{onClose:v}=e;v&&Go(v,p)}}const s={setTextContent(p){const{value:v}=n;v&&(v.textContent=p)}},g=Mo("Tag",d,r),c=K(()=>{const{type:p,size:v,color:{color:S,textColor:k}={}}=e,{common:{cubicBezierEaseInOut:y},self:{padding:w,closeMargin:$,closeMarginRtl:u,borderRadius:b,opacityDisabled:x,textColorCheckable:O,textColorHoverCheckable:E,textColorPressedCheckable:A,textColorChecked:P,colorCheckable:N,colorHoverCheckable:F,colorPressedCheckable:j,colorChecked:oe,colorCheckedHover:ne,colorCheckedPressed:te,closeBorderRadius:V,fontWeightStrong:re,[B("colorBordered",p)]:W,[B("closeSize",v)]:U,[B("closeIconSize",v)]:le,[B("fontSize",v)]:ae,[B("height",v)]:q,[B("color",p)]:ie,[B("textColor",p)]:se,[B("border",p)]:ce,[B("closeIconColor",p)]:Y,[B("closeIconColorHover",p)]:de,[B("closeIconColorPressed",p)]:ue,[B("closeColorHover",p)]:he,[B("closeColorPressed",p)]:fe}}=l.value;return{"--n-font-weight-strong":re,"--n-avatar-size-override":`calc(${q} - 8px)`,"--n-bezier":y,"--n-border-radius":b,"--n-border":ce,"--n-close-icon-size":le,"--n-close-color-pressed":fe,"--n-close-color-hover":he,"--n-close-border-radius":V,"--n-close-icon-color":Y,"--n-close-icon-color-hover":de,"--n-close-icon-color-pressed":ue,"--n-close-icon-color-disabled":Y,"--n-close-margin":$,"--n-close-margin-rtl":u,"--n-close-size":U,"--n-color":S||(o.value?W:ie),"--n-color-checkable":N,"--n-color-checked":oe,"--n-color-checked-hover":ne,"--n-color-checked-pressed":te,"--n-color-hover-checkable":F,"--n-color-pressed-checkable":j,"--n-font-size":ae,"--n-height":q,"--n-opacity-disabled":x,"--n-padding":w,"--n-text-color":k||se,"--n-text-color-checkable":O,"--n-text-color-checked":P,"--n-text-color-hover-checkable":E,"--n-text-color-pressed-checkable":A}}),m=i?Fe("tag",K(()=>{let p="";const{type:v,size:S,color:{color:k,textColor:y}={}}=e;return p+=v[0],p+=S[0],k&&(p+=`a${ze(k)}`),y&&(p+=`b${ze(y)}`),o.value&&(p+="c"),p}),c,e):void 0;return Object.assign(Object.assign({},s),{rtlEnabled:g,mergedClsPrefix:r,contentRef:n,mergedBordered:o,handleClick:t,handleCloseClick:f,cssVars:i?void 0:c,themeClass:m==null?void 0:m.themeClass,onRender:m==null?void 0:m.onRender})},render(){var e,n;const{mergedClsPrefix:o,rtlEnabled:r,closable:i,color:{borderColor:d}={},round:l,onRender:t,$slots:f}=this;t==null||t();const s=Ie(f.avatar,c=>c&&C("div",{class:`${o}-tag__avatar`},c)),g=Ie(f.icon,c=>c&&C("div",{class:`${o}-tag__icon`},c));return C("div",{class:[`${o}-tag`,this.themeClass,{[`${o}-tag--rtl`]:r,[`${o}-tag--strong`]:this.strong,[`${o}-tag--disabled`]:this.disabled,[`${o}-tag--checkable`]:this.checkable,[`${o}-tag--checked`]:this.checkable&&this.checked,[`${o}-tag--round`]:l,[`${o}-tag--avatar`]:s,[`${o}-tag--icon`]:g,[`${o}-tag--closable`]:i}],style:this.cssVars,onClick:this.handleClick,onMouseenter:this.onMouseenter,onMouseleave:this.onMouseleave},g||s,C("span",{class:`${o}-tag__content`,ref:"contentRef"},(n=(e=this.$slots).default)===null||n===void 0?void 0:n.call(e)),!this.checkable&&i?C(No,{clsPrefix:o,class:`${o}-tag__close`,disabled:this.disabled,onClick:this.handleCloseClick,focusable:this.internalCloseFocusable,round:l,isButtonTag:this.internalCloseIsButtonTag,absolute:!0}):null,!this.checkable&&this.mergedBordered?C("div",{class:`${o}-tag__border`,style:{borderColor:d}}):null)}}),Tn=L([T("base-selection",`
 position: relative;
 z-index: auto;
 box-shadow: none;
 width: 100%;
 max-width: 100%;
 display: inline-block;
 vertical-align: bottom;
 border-radius: var(--n-border-radius);
 min-height: var(--n-height);
 line-height: 1.5;
 font-size: var(--n-font-size);
 `,[T("base-loading",`
 color: var(--n-loading-color);
 `),T("base-selection-tags","min-height: var(--n-height);"),z("border, state-border",`
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 pointer-events: none;
 border: var(--n-border);
 border-radius: inherit;
 transition:
 box-shadow .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 `),z("state-border",`
 z-index: 1;
 border-color: #0000;
 `),T("base-suffix",`
 cursor: pointer;
 position: absolute;
 top: 50%;
 transform: translateY(-50%);
 right: 10px;
 `,[z("arrow",`
 font-size: var(--n-arrow-size);
 color: var(--n-arrow-color);
 transition: color .3s var(--n-bezier);
 `)]),T("base-selection-overlay",`
 display: flex;
 align-items: center;
 white-space: nowrap;
 pointer-events: none;
 position: absolute;
 top: 0;
 right: 0;
 bottom: 0;
 left: 0;
 padding: var(--n-padding-single);
 transition: color .3s var(--n-bezier);
 `,[z("wrapper",`
 flex-basis: 0;
 flex-grow: 1;
 overflow: hidden;
 text-overflow: ellipsis;
 `)]),T("base-selection-placeholder",`
 color: var(--n-placeholder-color);
 `,[z("inner",`
 max-width: 100%;
 overflow: hidden;
 `)]),T("base-selection-tags",`
 cursor: pointer;
 outline: none;
 box-sizing: border-box;
 position: relative;
 z-index: auto;
 display: flex;
 padding: var(--n-padding-multiple);
 flex-wrap: wrap;
 align-items: center;
 width: 100%;
 vertical-align: bottom;
 background-color: var(--n-color);
 border-radius: inherit;
 transition:
 color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 `),T("base-selection-label",`
 height: var(--n-height);
 display: inline-flex;
 width: 100%;
 vertical-align: bottom;
 cursor: pointer;
 outline: none;
 z-index: auto;
 box-sizing: border-box;
 position: relative;
 transition:
 color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 border-radius: inherit;
 background-color: var(--n-color);
 align-items: center;
 `,[T("base-selection-input",`
 font-size: inherit;
 line-height: inherit;
 outline: none;
 cursor: pointer;
 box-sizing: border-box;
 border:none;
 width: 100%;
 padding: var(--n-padding-single);
 background-color: #0000;
 color: var(--n-text-color);
 transition: color .3s var(--n-bezier);
 caret-color: var(--n-caret-color);
 `,[z("content",`
 text-overflow: ellipsis;
 overflow: hidden;
 white-space: nowrap; 
 `)]),z("render-label",`
 color: var(--n-text-color);
 `)]),G("disabled",[L("&:hover",[z("state-border",`
 box-shadow: var(--n-box-shadow-hover);
 border: var(--n-border-hover);
 `)]),M("focus",[z("state-border",`
 box-shadow: var(--n-box-shadow-focus);
 border: var(--n-border-focus);
 `)]),M("active",[z("state-border",`
 box-shadow: var(--n-box-shadow-active);
 border: var(--n-border-active);
 `),T("base-selection-label","background-color: var(--n-color-active);"),T("base-selection-tags","background-color: var(--n-color-active);")])]),M("disabled","cursor: not-allowed;",[z("arrow",`
 color: var(--n-arrow-color-disabled);
 `),T("base-selection-label",`
 cursor: not-allowed;
 background-color: var(--n-color-disabled);
 `,[T("base-selection-input",`
 cursor: not-allowed;
 color: var(--n-text-color-disabled);
 `),z("render-label",`
 color: var(--n-text-color-disabled);
 `)]),T("base-selection-tags",`
 cursor: not-allowed;
 background-color: var(--n-color-disabled);
 `),T("base-selection-placeholder",`
 cursor: not-allowed;
 color: var(--n-placeholder-color-disabled);
 `)]),T("base-selection-input-tag",`
 height: calc(var(--n-height) - 6px);
 line-height: calc(var(--n-height) - 6px);
 outline: none;
 display: none;
 position: relative;
 margin-bottom: 3px;
 max-width: 100%;
 vertical-align: bottom;
 `,[z("input",`
 font-size: inherit;
 font-family: inherit;
 min-width: 1px;
 padding: 0;
 background-color: #0000;
 outline: none;
 border: none;
 max-width: 100%;
 overflow: hidden;
 width: 1em;
 line-height: inherit;
 cursor: pointer;
 color: var(--n-text-color);
 caret-color: var(--n-caret-color);
 `),z("mirror",`
 position: absolute;
 left: 0;
 top: 0;
 white-space: pre;
 visibility: hidden;
 user-select: none;
 -webkit-user-select: none;
 opacity: 0;
 `)]),["warning","error"].map(e=>M(`${e}-status`,[z("state-border",`border: var(--n-border-${e});`),G("disabled",[L("&:hover",[z("state-border",`
 box-shadow: var(--n-box-shadow-hover-${e});
 border: var(--n-border-hover-${e});
 `)]),M("active",[z("state-border",`
 box-shadow: var(--n-box-shadow-active-${e});
 border: var(--n-border-active-${e});
 `),T("base-selection-label",`background-color: var(--n-color-active-${e});`),T("base-selection-tags",`background-color: var(--n-color-active-${e});`)]),M("focus",[z("state-border",`
 box-shadow: var(--n-box-shadow-focus-${e});
 border: var(--n-border-focus-${e});
 `)])])]))]),T("base-selection-popover",`
 margin-bottom: -3px;
 display: flex;
 flex-wrap: wrap;
 margin-right: -8px;
 `),T("base-selection-tag-wrapper",`
 max-width: 100%;
 display: inline-flex;
 padding: 0 7px 3px 0;
 `,[L("&:last-child","padding-right: 0;"),T("tag",`
 font-size: 14px;
 max-width: 100%;
 `,[z("content",`
 line-height: 1.25;
 text-overflow: ellipsis;
 overflow: hidden;
 `)])])]),Dn=ee({name:"InternalSelection",props:Object.assign(Object.assign({},Q.props),{clsPrefix:{type:String,required:!0},bordered:{type:Boolean,default:void 0},active:Boolean,pattern:{type:String,default:""},placeholder:String,selectedOption:{type:Object,default:null},selectedOptions:{type:Array,default:null},labelField:{type:String,default:"label"},valueField:{type:String,default:"value"},multiple:Boolean,filterable:Boolean,clearable:Boolean,disabled:Boolean,size:{type:String,default:"medium"},loading:Boolean,autofocus:Boolean,showArrow:{type:Boolean,default:!0},inputProps:Object,focused:Boolean,renderTag:Function,onKeydown:Function,onClick:Function,onBlur:Function,onFocus:Function,onDeleteOption:Function,maxTagCount:[String,Number],onClear:Function,onPatternInput:Function,onPatternFocus:Function,onPatternBlur:Function,renderLabel:Function,status:String,inlineThemeDisabled:Boolean,ignoreComposition:{type:Boolean,default:!0},onResize:Function}),setup(e){const n=_(null),o=_(null),r=_(null),i=_(null),d=_(null),l=_(null),t=_(null),f=_(null),s=_(null),g=_(null),c=_(!1),m=_(!1),p=_(!1),v=Q("InternalSelection","-internal-selection",Tn,Lo,e,J(e,"clsPrefix")),S=K(()=>e.clearable&&!e.disabled&&(p.value||e.active)),k=K(()=>e.selectedOption?e.renderTag?e.renderTag({option:e.selectedOption,handleClose:()=>{}}):e.renderLabel?e.renderLabel(e.selectedOption,!0):Z(e.selectedOption[e.labelField],e.selectedOption,!0):e.placeholder),y=K(()=>{const a=e.selectedOption;if(!!a)return a[e.labelField]}),w=K(()=>e.multiple?!!(Array.isArray(e.selectedOptions)&&e.selectedOptions.length):e.selectedOption!==null);function $(){var a;const{value:h}=n;if(h){const{value:R}=o;R&&(R.style.width=`${h.offsetWidth}px`,e.maxTagCount!=="responsive"&&((a=s.value)===null||a===void 0||a.sync()))}}function u(){const{value:a}=g;a&&(a.style.display="none")}function b(){const{value:a}=g;a&&(a.style.display="inline-block")}ge(J(e,"active"),a=>{a||u()}),ge(J(e,"pattern"),()=>{e.multiple&&Re($)});function x(a){const{onFocus:h}=e;h&&h(a)}function O(a){const{onBlur:h}=e;h&&h(a)}function E(a){const{onDeleteOption:h}=e;h&&h(a)}function A(a){const{onClear:h}=e;h&&h(a)}function P(a){const{onPatternInput:h}=e;h&&h(a)}function N(a){var h;(!a.relatedTarget||!(!((h=r.value)===null||h===void 0)&&h.contains(a.relatedTarget)))&&x(a)}function F(a){var h;!((h=r.value)===null||h===void 0)&&h.contains(a.relatedTarget)||O(a)}function j(a){A(a)}function oe(){p.value=!0}function ne(){p.value=!1}function te(a){!e.active||!e.filterable||a.target!==o.value&&a.preventDefault()}function V(a){E(a)}function re(a){if(a.key==="Backspace"&&!W.value&&!e.pattern.length){const{selectedOptions:h}=e;h!=null&&h.length&&V(h[h.length-1])}}const W=_(!1);let U=null;function le(a){const{value:h}=n;if(h){const R=a.target.value;h.textContent=R,$()}e.ignoreComposition&&W.value?U=a:P(a)}function ae(){W.value=!0}function q(){W.value=!1,e.ignoreComposition&&P(U),U=null}function ie(a){var h;m.value=!0,(h=e.onPatternFocus)===null||h===void 0||h.call(e,a)}function se(a){var h;m.value=!1,(h=e.onPatternBlur)===null||h===void 0||h.call(e,a)}function ce(){var a,h;if(e.filterable)m.value=!1,(a=l.value)===null||a===void 0||a.blur(),(h=o.value)===null||h===void 0||h.blur();else if(e.multiple){const{value:R}=i;R==null||R.blur()}else{const{value:R}=d;R==null||R.blur()}}function Y(){var a,h,R;e.filterable?(m.value=!1,(a=l.value)===null||a===void 0||a.focus()):e.multiple?(h=i.value)===null||h===void 0||h.focus():(R=d.value)===null||R===void 0||R.focus()}function de(){const{value:a}=o;a&&(b(),a.focus())}function ue(){const{value:a}=o;a&&a.blur()}function he(a){const{value:h}=t;h&&h.setTextContent(`+${a}`)}function fe(){const{value:a}=f;return a}function Be(){return o.value}let be=null;function ve(){be!==null&&window.clearTimeout(be)}function Me(){e.disabled||e.active||(ve(),be=window.setTimeout(()=>{w.value&&(c.value=!0)},100))}function Ne(){ve()}function Ke(a){a||(ve(),c.value=!1)}ge(w,a=>{a||(c.value=!1)}),we(()=>{Wo(()=>{const a=l.value;!a||(a.tabIndex=e.disabled||m.value?-1:0)})}),Jo(r,e.onResize);const{inlineThemeDisabled:Pe}=e,Se=K(()=>{const{size:a}=e,{common:{cubicBezierEaseInOut:h},self:{borderRadius:R,color:Le,placeholderColor:We,textColor:He,paddingSingle:De,paddingMultiple:Ge,caretColor:je,colorDisabled:Ue,textColorDisabled:Ve,placeholderColorDisabled:qe,colorActive:Ye,boxShadowFocus:Ze,boxShadowActive:Je,boxShadowHover:Qe,border:Xe,borderFocus:eo,borderHover:oo,borderActive:no,arrowColor:to,arrowColorDisabled:ro,loadingColor:lo,colorActiveWarning:ao,boxShadowFocusWarning:io,boxShadowActiveWarning:so,boxShadowHoverWarning:co,borderWarning:uo,borderFocusWarning:ho,borderHoverWarning:fo,borderActiveWarning:bo,colorActiveError:vo,boxShadowFocusError:go,boxShadowActiveError:po,boxShadowHoverError:Co,borderError:mo,borderFocusError:yo,borderHoverError:xo,borderActiveError:wo,clearColor:ko,clearColorHover:Po,clearColorPressed:So,clearSize:Io,arrowSize:zo,[B("height",a)]:$o,[B("fontSize",a)]:To}}=v.value;return{"--n-bezier":h,"--n-border":Xe,"--n-border-active":no,"--n-border-focus":eo,"--n-border-hover":oo,"--n-border-radius":R,"--n-box-shadow-active":Je,"--n-box-shadow-focus":Ze,"--n-box-shadow-hover":Qe,"--n-caret-color":je,"--n-color":Le,"--n-color-active":Ye,"--n-color-disabled":Ue,"--n-font-size":To,"--n-height":$o,"--n-padding-single":De,"--n-padding-multiple":Ge,"--n-placeholder-color":We,"--n-placeholder-color-disabled":qe,"--n-text-color":He,"--n-text-color-disabled":Ve,"--n-arrow-color":to,"--n-arrow-color-disabled":ro,"--n-loading-color":lo,"--n-color-active-warning":ao,"--n-box-shadow-focus-warning":io,"--n-box-shadow-active-warning":so,"--n-box-shadow-hover-warning":co,"--n-border-warning":uo,"--n-border-focus-warning":ho,"--n-border-hover-warning":fo,"--n-border-active-warning":bo,"--n-color-active-error":vo,"--n-box-shadow-focus-error":go,"--n-box-shadow-active-error":po,"--n-box-shadow-hover-error":Co,"--n-border-error":mo,"--n-border-focus-error":yo,"--n-border-hover-error":xo,"--n-border-active-error":wo,"--n-clear-size":Io,"--n-clear-color":ko,"--n-clear-color-hover":Po,"--n-clear-color-pressed":So,"--n-arrow-size":zo}}),D=Pe?Fe("internal-selection",K(()=>e.size[0]),Se,e):void 0;return{mergedTheme:v,mergedClearable:S,patternInputFocused:m,filterablePlaceholder:k,label:y,selected:w,showTagsPanel:c,isComposing:W,counterRef:t,counterWrapperRef:f,patternInputMirrorRef:n,patternInputRef:o,selfRef:r,multipleElRef:i,singleElRef:d,patternInputWrapperRef:l,overflowRef:s,inputTagElRef:g,handleMouseDown:te,handleFocusin:N,handleClear:j,handleMouseEnter:oe,handleMouseLeave:ne,handleDeleteOption:V,handlePatternKeyDown:re,handlePatternInputInput:le,handlePatternInputBlur:se,handlePatternInputFocus:ie,handleMouseEnterCounter:Me,handleMouseLeaveCounter:Ne,handleFocusout:F,handleCompositionEnd:q,handleCompositionStart:ae,onPopoverUpdateShow:Ke,focus:Y,focusInput:de,blur:ce,blurInput:ue,updateCounter:he,getCounter:fe,getTail:Be,renderLabel:e.renderLabel,cssVars:Pe?void 0:Se,themeClass:D==null?void 0:D.themeClass,onRender:D==null?void 0:D.onRender}},render(){const{status:e,multiple:n,size:o,disabled:r,filterable:i,maxTagCount:d,bordered:l,clsPrefix:t,onRender:f,renderTag:s,renderLabel:g}=this;f==null||f();const c=d==="responsive",m=typeof d=="number",p=c||m,v=C(Do,null,{default:()=>C(Yo,{clsPrefix:t,loading:this.loading,showArrow:this.showArrow,showClear:this.mergedClearable&&this.selected,onClear:this.handleClear},{default:()=>{var k,y;return(y=(k=this.$slots).arrow)===null||y===void 0?void 0:y.call(k)}})});let S;if(n){const{labelField:k}=this,y=F=>C("div",{class:`${t}-base-selection-tag-wrapper`,key:F.value},s?s({option:F,handleClose:()=>this.handleDeleteOption(F)}):C(ye,{size:o,closable:!F.disabled,disabled:r,onClose:()=>this.handleDeleteOption(F),internalCloseIsButtonTag:!1,internalCloseFocusable:!1},{default:()=>g?g(F,!0):Z(F[k],F,!0)})),w=(m?this.selectedOptions.slice(0,d):this.selectedOptions).map(y),$=i?C("div",{class:`${t}-base-selection-input-tag`,ref:"inputTagElRef",key:"__input-tag__"},C("input",Object.assign({},this.inputProps,{ref:"patternInputRef",tabindex:-1,disabled:r,value:this.pattern,autofocus:this.autofocus,class:`${t}-base-selection-input-tag__input`,onBlur:this.handlePatternInputBlur,onFocus:this.handlePatternInputFocus,onKeydown:this.handlePatternKeyDown,onInput:this.handlePatternInputInput,onCompositionstart:this.handleCompositionStart,onCompositionend:this.handleCompositionEnd})),C("span",{ref:"patternInputMirrorRef",class:`${t}-base-selection-input-tag__mirror`},this.pattern)):null,u=c?()=>C("div",{class:`${t}-base-selection-tag-wrapper`,ref:"counterWrapperRef"},C(ye,{size:o,ref:"counterRef",onMouseenter:this.handleMouseEnterCounter,onMouseleave:this.handleMouseLeaveCounter,disabled:r})):void 0;let b;if(m){const F=this.selectedOptions.length-d;F>0&&(b=C("div",{class:`${t}-base-selection-tag-wrapper`,key:"__counter__"},C(ye,{size:o,ref:"counterRef",onMouseenter:this.handleMouseEnterCounter,disabled:r},{default:()=>`+${F}`})))}const x=c?i?C(Te,{ref:"overflowRef",updateCounter:this.updateCounter,getCounter:this.getCounter,getTail:this.getTail,style:{width:"100%",display:"flex",overflow:"hidden"}},{default:()=>w,counter:u,tail:()=>$}):C(Te,{ref:"overflowRef",updateCounter:this.updateCounter,getCounter:this.getCounter,style:{width:"100%",display:"flex",overflow:"hidden"}},{default:()=>w,counter:u}):m?w.concat(b):w,O=p?()=>C("div",{class:`${t}-base-selection-popover`},c?w:this.selectedOptions.map(y)):void 0,E=p?{show:this.showTagsPanel,trigger:"hover",overlap:!0,placement:"top",width:"trigger",onUpdateShow:this.onPopoverUpdateShow,theme:this.mergedTheme.peers.Popover,themeOverrides:this.mergedTheme.peerOverrides.Popover}:null,P=(this.selected?!1:this.active?!this.pattern&&!this.isComposing:!0)?C("div",{class:`${t}-base-selection-placeholder ${t}-base-selection-overlay`},C("div",{class:`${t}-base-selection-placeholder__inner`},this.placeholder)):null,N=i?C("div",{ref:"patternInputWrapperRef",class:`${t}-base-selection-tags`},x,c?null:$,v):C("div",{ref:"multipleElRef",class:`${t}-base-selection-tags`,tabindex:r?void 0:0},x,v);S=C(Ho,null,p?C(Vo,Object.assign({},E,{scrollable:!0,style:"max-height: calc(var(--v-target-height) * 6.6);"}),{trigger:()=>N,default:O}):N,P)}else if(i){const k=this.pattern||this.isComposing,y=this.active?!k:!this.selected,w=this.active?!1:this.selected;S=C("div",{ref:"patternInputWrapperRef",class:`${t}-base-selection-label`},C("input",Object.assign({},this.inputProps,{ref:"patternInputRef",class:`${t}-base-selection-input`,value:this.active?this.pattern:"",placeholder:"",readonly:r,disabled:r,tabindex:-1,autofocus:this.autofocus,onFocus:this.handlePatternInputFocus,onBlur:this.handlePatternInputBlur,onInput:this.handlePatternInputInput,onCompositionstart:this.handleCompositionStart,onCompositionend:this.handleCompositionEnd})),w?C("div",{class:`${t}-base-selection-label__render-label ${t}-base-selection-overlay`,key:"input"},C("div",{class:`${t}-base-selection-overlay__wrapper`},s?s({option:this.selectedOption,handleClose:()=>{}}):g?g(this.selectedOption,!0):Z(this.label,this.selectedOption,!0))):null,y?C("div",{class:`${t}-base-selection-placeholder ${t}-base-selection-overlay`,key:"placeholder"},C("div",{class:`${t}-base-selection-overlay__wrapper`},this.filterablePlaceholder)):null,v)}else S=C("div",{ref:"singleElRef",class:`${t}-base-selection-label`,tabindex:this.disabled?void 0:0},this.label!==void 0?C("div",{class:`${t}-base-selection-input`,title:qo(this.label),key:"input"},C("div",{class:`${t}-base-selection-input__content`},s?s({option:this.selectedOption,handleClose:()=>{}}):g?g(this.selectedOption,!0):Z(this.label,this.selectedOption,!0))):C("div",{class:`${t}-base-selection-placeholder ${t}-base-selection-overlay`,key:"placeholder"},C("div",{class:`${t}-base-selection-placeholder__inner`},this.placeholder)),v);return C("div",{ref:"selfRef",class:[`${t}-base-selection`,this.themeClass,e&&`${t}-base-selection--${e}-status`,{[`${t}-base-selection--active`]:this.active,[`${t}-base-selection--selected`]:this.selected||this.active&&this.pattern,[`${t}-base-selection--disabled`]:this.disabled,[`${t}-base-selection--multiple`]:this.multiple,[`${t}-base-selection--focus`]:this.focused}],style:this.cssVars,onClick:this.onClick,onMouseenter:this.handleMouseEnter,onMouseleave:this.handleMouseLeave,onKeydown:this.onKeydown,onFocusin:this.handleFocusin,onFocusout:this.handleFocusout,onMousedown:this.handleMouseDown},S,l?C("div",{class:`${t}-base-selection__border`}):null,l?C("div",{class:`${t}-base-selection__state-border`}):null)}});export{Ln as F,Dn as N,dn as S,Wn as a,ye as b,Hn as c,yn as f,Kn as h,Jo as u};
