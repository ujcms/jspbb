// AJAX 请求全部加上 CSRF TOKEN
$(function () {
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader('X-XSRF-TOKEN', Cookies.get('XSRF-TOKEN'));
    });
});

// jquery-validation 和 bootstrap4 整合
if ($.validator) {
    $.validator.setDefaults({
        errorElement: 'div',
        errorPlacement: function (error, element) {
            error.addClass('invalid-feedback');
            // 错误提示信息直接放在元素后面，或者放在.input-gourp后面
            var inputGroup = $(element).parent('.input-group');
            inputGroup.length > 0 ? inputGroup.after(error) : element.after(error);
        },
        highlight: function (element, errorClass, validClass) {
            $(element).closest('.form-control').addClass('is-invalid');
            // $( element ).closest( '.form-group' ).addClass( 'has-danger' );
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).closest('.form-control').removeClass('is-invalid');
            // $( element ).closest( '.form-group' ).removeClass( 'has-danger' );
        }
    });
    // 日期验证 2008-01-01 或 2008-01-01 08:00:00 或 2008-01-01T08:00:00
    $.validator.addMethod('date', function (value, element) {
        return this.optional(element) || /^\d{4}\-(0[1-9]|1[012])\-(0[1-9]|[12][0-9]|3[01])([ T]([01][0-9]|2[0-3])\:[0-5][0-9]\:[0-5][0-9])?$/.test(value);
    });
    // 覆盖 step 方法。因为日期控件flatpickr在手机浏览器下会自动给input表单加上step参数，导致验证失败。另外这个校验基本没有应用场合。
    $.validator.methods.step = function (value, element) {
        return true;
    };
}

/**
 * 是否允许浏览器发送通知
 */
function isNotificationAllowed() {
    if ('Notification' in window) {
        if (Notification.permission === 'granted') {
            return true;
        } else if (Notification.permission !== 'denied') {
            Notification.requestPermission(function (permission) {
                if (permission === 'granted') {
                    return true;
                }
            });
        }
    }
    return false;
}

/**
 * CodeMirror 粘贴图片时上传图片
 *
 * @param cm CodeMirror对象
 * @param uploadUrl 上传地址
 */
function codemirrorPaste(cm, uploadUrl) {
    var userAgent = navigator.userAgent;
    var chrome = /Chrome\//.test(userAgent);
    // /Firefox/i.test(userAgent)
    var gecko = /gecko\/\d/i.test(userAgent);
    var trident = /Trident/i.test(userAgent);
    var base64ToBlob = function (base64) {
        var index = base64.lastIndexOf(',');
        if (index >= 0) {
            base64 = base64.substring(index + 1);
        }
        var binary = window.atob(base64);
        var len = binary.length;
        var buffer = new ArrayBuffer(len);
        var view = new Uint8Array(buffer);
        for (var i = 0; i < len; i++) {
            view[i] = binary.charCodeAt(i);
        }
        return new Blob([view], {
            type: 'image/png'
        });
        // return new Blob([buffer], {type : 'image/png'});
    };
    var xhrImageUpload = function (base64, cm) {
        var formData = new FormData();
        formData.append('file', base64ToBlob(base64), 'blob.png');
        // 不可加 Content-Type:multipart/form-data，加上任何 ContentType 都会出错。
        request(uploadUrl, {method: 'POST', body: formData}).then(function(data) {
            if(data===null) return;
            cm.replaceSelection('![](' + data.fileUrl + ')');
        });
        // var xhr = new XMLHttpRequest();
        // xhr.onreadystatechange = function () {
        //     if (xhr.readyState === 4) {
        //         var json = $.parseJSON(xhr.responseText);
        //         if (xhr.status === 200) {
        //             if (json.fileUrl) {
        //                 cm.replaceSelection('![](' + json.fileUrl + ')');
        //             } else {
        //                 showError(json.message);
        //             }
        //         } else {
        //             showErrorPre({responseJSON: json});
        //         }
        //     }
        // };
        // xhr.upload.onerror = function (e) {
        //     alert('upload image error!');
        // };
        // xhr.open('POST', uploadUrl, true);
        // xhr.setRequestHeader('Cache-Control', 'no-cache');
        // xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
        // xhr.setRequestHeader('X-XSRF-TOKEN', Cookies.get('XSRF-TOKEN'));
        // xhr.send(formData);
    };
    // ie6-10获取剪切板的方式： window.clipboardData.getData(type)
    var forChrome = function (cm, event) {
        var items = event.clipboardData.items;
        for (var i = 0, len = items.length; i < len; i++) {
            var item = items[i];
            if (item.type.match(/^image\//)) {
                var file = item.getAsFile();
                var reader = new FileReader();
                reader.onload = function (e) {
                    var base64 = e.target.result;
                    xhrImageUpload(base64, cm);
                };
                reader.readAsDataURL(file);
            }
        }
    };
    var pasteDiv = $('<div contenteditable="true"/>').css({
        'overflow': 'hidden',
        'height': '0'
    }).appendTo(document.body);
    var forFirefox = function (cm, event) {
        if ((event.ctrlKey || event.metaKey)
            && (event.keyCode === 86 || event.key === 'v')) {
            pasteDiv.focus();
            setTimeout(function () {
                var isBase64 = false;
                var imgs = pasteDiv.find('img');
                cm.focus();
                if (imgs && imgs.length > 0) {
                    var base64 = imgs[0].src;
                    if (/^data:image/i.test(base64)) {
                        isBase64 = true;
                        xhrImageUpload(base64, cm);
                    }
                }
                if (!isBase64) {
                    cm.replaceSelection(pasteDiv.text());
                }
                pasteDiv.empty();
            }, 5);
        }
    };
    if (chrome) {
        cm.on('paste', function (cm, e) {
            forChrome(cm, e);
            return false;
        });
    } else if (gecko) {
        cm.on('keypress', function (cm, e) {
            forFirefox(cm, e);
            return false;
        });
    } else if (trident) {
        cm.on('keydown', function (cm, e) {
            forFirefox(cm, e);
            return false;
        });
    }
}