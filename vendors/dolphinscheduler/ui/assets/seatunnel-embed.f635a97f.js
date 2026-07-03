import{ap as q,cw as K,ab as p,ae as I,b as $,K as u,q as R,bV as D,m as Z,d as H,e as G,f as A,x as J,g as W,j as Q,k as P,h as a,ac as X,R as Y,Z as ee,N as oe,a1 as re,a0 as ne,_ as te,$ as se,z as m,bq as le,V as ie,u as ae,c as x,an as ce}from"./index.0a188daf.js";import{s as de}from"./service.0e18c895.js";import{r as ue,a as he}from"./resolve-slot.808959e0.js";import{g as fe}from"./index.43292a93.js";import{N as ge}from"./Empty.77e9a983.js";import{N as me}from"./Spin.03496cf1.js";import"./ui-setting.b6696365.js";import"./lodash.db6e40d7.js";import"./use-locale.5376a083.js";import"./fade-in.cssr.d1d7e6f0.js";import"./use-compitable.2fb00d05.js";const be=e=>{const{lineHeight:r,borderRadius:s,fontWeightStrong:c,baseColor:t,dividerColor:b,actionColor:l,textColor1:d,textColor2:o,closeColorHover:h,closeColorPressed:f,closeIconColor:v,closeIconColorHover:C,closeIconColorPressed:i,infoColor:n,successColor:y,warningColor:z,errorColor:_,fontSize:T}=e;return Object.assign(Object.assign({},K),{fontSize:T,lineHeight:r,titleFontWeight:c,borderRadius:s,border:`1px solid ${b}`,color:l,titleTextColor:d,iconColor:o,contentTextColor:o,closeBorderRadius:s,closeColorHover:h,closeColorPressed:f,closeIconColor:v,closeIconColorHover:C,closeIconColorPressed:i,borderInfo:`1px solid ${p(t,I(n,{alpha:.25}))}`,colorInfo:p(t,I(n,{alpha:.08})),titleTextColorInfo:d,iconColorInfo:n,contentTextColorInfo:o,closeColorHoverInfo:h,closeColorPressedInfo:f,closeIconColorInfo:v,closeIconColorHoverInfo:C,closeIconColorPressedInfo:i,borderSuccess:`1px solid ${p(t,I(y,{alpha:.25}))}`,colorSuccess:p(t,I(y,{alpha:.08})),titleTextColorSuccess:d,iconColorSuccess:y,contentTextColorSuccess:o,closeColorHoverSuccess:h,closeColorPressedSuccess:f,closeIconColorSuccess:v,closeIconColorHoverSuccess:C,closeIconColorPressedSuccess:i,borderWarning:`1px solid ${p(t,I(z,{alpha:.33}))}`,colorWarning:p(t,I(z,{alpha:.08})),titleTextColorWarning:d,iconColorWarning:z,contentTextColorWarning:o,closeColorHoverWarning:h,closeColorPressedWarning:f,closeIconColorWarning:v,closeIconColorHoverWarning:C,closeIconColorPressedWarning:i,borderError:`1px solid ${p(t,I(_,{alpha:.25}))}`,colorError:p(t,I(_,{alpha:.08})),titleTextColorError:d,iconColorError:_,contentTextColorError:o,closeColorHoverError:h,closeColorPressedError:f,closeIconColorError:v,closeIconColorHoverError:C,closeIconColorPressedError:i})},ve={name:"Alert",common:q,self:be},Ce=ve,pe=$("alert",`
 line-height: var(--n-line-height);
 border-radius: var(--n-border-radius);
 position: relative;
 transition: background-color .3s var(--n-bezier);
 background-color: var(--n-color);
 text-align: start;
 word-break: break-word;
`,[u("border",`
 border-radius: inherit;
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 transition: border-color .3s var(--n-bezier);
 border: var(--n-border);
 pointer-events: none;
 `),R("closable",[$("alert-body",[u("title",`
 padding-right: 24px;
 `)])]),u("icon",{color:"var(--n-icon-color)"}),$("alert-body",{padding:"var(--n-padding)"},[u("title",{color:"var(--n-title-text-color)"}),u("content",{color:"var(--n-content-text-color)"})]),D({originalTransition:"transform .3s var(--n-bezier)",enterToProps:{transform:"scale(1)"},leaveToProps:{transform:"scale(0.9)"}}),u("icon",`
 position: absolute;
 left: 0;
 top: 0;
 align-items: center;
 justify-content: center;
 display: flex;
 width: var(--n-icon-size);
 height: var(--n-icon-size);
 font-size: var(--n-icon-size);
 margin: var(--n-icon-margin);
 `),u("close",`
 transition:
 color .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 position: absolute;
 right: 0;
 top: 0;
 margin: var(--n-close-margin);
 `),R("show-icon",[$("alert-body",{paddingLeft:"calc(var(--n-icon-margin-left) + var(--n-icon-size) + var(--n-icon-margin-right))"})]),$("alert-body",`
 border-radius: var(--n-border-radius);
 transition: border-color .3s var(--n-bezier);
 `,[u("title",`
 transition: color .3s var(--n-bezier);
 font-size: 16px;
 line-height: 19px;
 font-weight: var(--n-title-font-weight);
 `,[Z("& +",[u("content",{marginTop:"9px"})])]),u("content",{transition:"color .3s var(--n-bezier)",fontSize:"var(--n-font-size)"})]),u("icon",{transition:"color .3s var(--n-bezier)"})]),Ie=Object.assign(Object.assign({},A.props),{title:String,showIcon:{type:Boolean,default:!0},type:{type:String,default:"default"},bordered:{type:Boolean,default:!0},closable:Boolean,onClose:Function,onAfterLeave:Function,onAfterHide:Function}),w=H({name:"Alert",inheritAttrs:!1,props:Ie,setup(e){const{mergedClsPrefixRef:r,mergedBorderedRef:s,inlineThemeDisabled:c,mergedRtlRef:t}=G(e),b=A("Alert","-alert",pe,Ce,e,r),l=J("Alert",t,r),d=W(()=>{const{common:{cubicBezierEaseInOut:i},self:n}=b.value,{fontSize:y,borderRadius:z,titleFontWeight:_,lineHeight:T,iconSize:B,iconMargin:E,iconMarginRtl:L,closeIconSize:k,closeBorderRadius:M,closeSize:N,closeMargin:j,closeMarginRtl:U,padding:O}=n,{type:g}=e,{left:V,right:F}=fe(E);return{"--n-bezier":i,"--n-color":n[m("color",g)],"--n-close-icon-size":k,"--n-close-border-radius":M,"--n-close-color-hover":n[m("closeColorHover",g)],"--n-close-color-pressed":n[m("closeColorPressed",g)],"--n-close-icon-color":n[m("closeIconColor",g)],"--n-close-icon-color-hover":n[m("closeIconColorHover",g)],"--n-close-icon-color-pressed":n[m("closeIconColorPressed",g)],"--n-icon-color":n[m("iconColor",g)],"--n-border":n[m("border",g)],"--n-title-text-color":n[m("titleTextColor",g)],"--n-content-text-color":n[m("contentTextColor",g)],"--n-line-height":T,"--n-border-radius":z,"--n-font-size":y,"--n-title-font-weight":_,"--n-icon-size":B,"--n-icon-margin":E,"--n-icon-margin-rtl":L,"--n-close-size":N,"--n-close-margin":j,"--n-close-margin-rtl":U,"--n-padding":O,"--n-icon-margin-left":V,"--n-icon-margin-right":F}}),o=c?Q("alert",W(()=>e.type[0]),d,e):void 0,h=P(!0),f=()=>{const{onAfterLeave:i,onAfterHide:n}=e;i&&i(),n&&n()};return{rtlEnabled:l,mergedClsPrefix:r,mergedBordered:s,visible:h,handleCloseClick:()=>{var i;Promise.resolve((i=e.onClose)===null||i===void 0?void 0:i.call(e)).then(n=>{n!==!1&&(h.value=!1)})},handleAfterLeave:()=>{f()},mergedTheme:b,cssVars:c?void 0:d,themeClass:o==null?void 0:o.themeClass,onRender:o==null?void 0:o.onRender}},render(){var e;return(e=this.onRender)===null||e===void 0||e.call(this),a(X,{onAfterLeave:this.handleAfterLeave},{default:()=>{const{mergedClsPrefix:r,$slots:s}=this,c={class:[`${r}-alert`,this.themeClass,this.closable&&`${r}-alert--closable`,this.showIcon&&`${r}-alert--show-icon`,this.rtlEnabled&&`${r}-alert--rtl`],style:this.cssVars,role:"alert"};return this.visible?a("div",Object.assign({},Y(this.$attrs,c)),this.closable&&a(ee,{clsPrefix:r,class:`${r}-alert__close`,onClick:this.handleCloseClick}),this.bordered&&a("div",{class:`${r}-alert__border`}),this.showIcon&&a("div",{class:`${r}-alert__icon`,"aria-hidden":"true"},ue(s.icon,()=>[a(oe,{clsPrefix:r},{default:()=>{switch(this.type){case"success":return a(se,null);case"info":return a(te,null);case"warning":return a(ne,null);case"error":return a(re,null);default:return null}}})])),a("div",{class:[`${r}-alert-body`,this.mergedBordered&&`${r}-alert-body--bordered`]},he(s.header,t=>{const b=t||this.title;return b?a("div",{class:`${r}-alert-body__title`},b):null}),s.default&&a("div",{class:`${r}-alert-body__content`},s))):null}})}});function xe(){return de.get("/seatunnel/api/v1/dolphinscheduler/seatunnel-ui/info",{baseURL:""})}function Se(e){const r=P(!0),s=P(""),c=P(""),t=le(),b=(l,d)=>{let o=l;if(!o.startsWith("http://")&&!o.startsWith("https://")){const C=o.startsWith("/")?o:`/${o}`;o=`${window.location.origin}${C}`}o=o.endsWith("/")?o.slice(0,-1):o;const h=d.startsWith("/")?d:`/${d}`,f=t.getSessionId,v=new URLSearchParams({embed:"1",datasourceSource:"dolphinscheduler"});return f&&v.set("dsSessionId",f),`${o}/#${h}?${v.toString()}`};return ie(async()=>{try{const l=await xe();if(!l.enabled||!l.embedBaseUrl){c.value=l.message||"SeaTunnel Web is not configured";return}s.value=b(l.embedBaseUrl,e)}catch(l){c.value=(l==null?void 0:l.message)||"Failed to load SeaTunnel UI"}finally{r.value=!1}}),{loading:r,embedUrl:s,errorMessage:c}}const ye="_container_sx1z3_1",ze="_hint_sx1z3_8",_e="_frame_sx1z3_13",$e="_empty_sx1z3_20",S={container:ye,hint:ze,frame:_e,empty:$e};function Pe(e){return typeof e=="function"||Object.prototype.toString.call(e)==="[object Object]"&&!ce(e)}const Ne=H({name:"SeatunnelEmbed",props:{seatunnelPath:{type:String,required:!0},titleKey:{type:String,required:!0}},setup(e){const{t:r}=ae(),{loading:s,embedUrl:c,errorMessage:t}=Se(e.seatunnelPath);return{t:r,loading:s,embedUrl:c,errorMessage:t}},render(){let e;return this.loading?x("div",{class:S.container},[x(me,{size:"large"},null)]):this.errorMessage?x("div",{class:S.container},[x(w,{type:"warning",title:this.t("seatunnel.unavailable")},{default:()=>[this.errorMessage]}),x(ge,{description:this.t("seatunnel.check_config"),class:S.empty},null)]):x("div",{class:S.container},[x(w,{type:"info",class:S.hint},Pe(e=this.t("seatunnel.datasource_hint"))?e:{default:()=>[e]}),x("iframe",{class:S.frame,src:this.embedUrl,title:this.t(this.titleKey)},null)])}});export{Ne as default};
