var SOCOUtils = {
    //校验字符串是否为空
    isBlank: function (text) {
        if (text == null) {
            return true;
        }
        if (text == "") {
            return true;
        }
        if (text.length == 0) {
            return true;
        }
        if (text == undefined) {
            return true;
        }
        return false;
    },
    //校验字符串是否不为空
    isNotBlank: function (text) {
        return !(this.isBlank(text))
    },
    isEquals:function(var1, var2){
        return var1 == var2;
    },
    //格式化金额
    formatCurrency: function (account) {
        if (this.isBlank(account)) {
            return '￥0.00';
        }
        var str = this.formatNumber(account) + '';
        var intSum = str.substring(0, str.indexOf(".")).replace(/\B(?=(?:\d{3})+$)/g, ',');//取到整数部分
        var dot = str.substring(str.length, str.indexOf("."))//取到小数部分搜索
        var ret = intSum + dot;
        return "￥" + ret;
    },
    //转换成小数
    formatNumber: function (x) {
        x = Number(parseFloat(x).toFixed(3).slice(0, -1));
        var f = parseFloat(x);
        if (isNaN(f)) {
            return false;
        }
        var f = Math.round(x * 100) / 100;
        var s = f.toString();
        var rs = s.indexOf('.');
        if (rs < 0) {
            rs = s.length;
            s += '.';
        }
        while (s.length <= rs + 2) {
            s += '0';
        }
        return s;
    },
    //判断一个对象是否为空
    objectIsBlank: function (obj) {
        if (JSON.stringify(obj) == "{}") {
            return true
        }
        if (Object.keys(obj).length == 0) {
            return true
        }
        return false
    },
    //判断一个对象是否不为空
    objectIsNotBlank: function (obj) {
        return !(this.objectIsBlank(obj))
    },
    //拿取form表单数据转成json格式返回(基于jquery)
    formToJson: function (select) {
        var arry = $(select).serializeArray();
        var data = {}
        arry.forEach(function (element, index) {
            if (data[element.name]) {
                data[element.name] = data[element.name] + '$#' + element.value;
            } else {
                data[element.name] = element.value;
            }
        })
        return data
    },
    formToJsonNoNUll: function (select) {
        var arry = $(select).serializeArray()
        var data = {}
        arry.forEach(function (element, index) {
            if (element.value != "") {
                data[element.name] = element.value
            }
        })
        return data
    },
    //补0操作
    getzf: function (num) {
        if (parseInt(num) < 10) {
            num = '0' + num;
        }
        return num;
    },
    //格式化日期	格式(yyyy-MM-dd hh:mm:ss)
    formatDate: function (fmt, date) {
        if (this.isBlank(date)) {
            date = new Date()
        }
        date = new Date(date)
        var o = {
            "M+": date.getMonth() + 1,                 //月份
            "d+": date.getDate(),                    //日
            "h+": date.getHours(),                   //小时
            "m+": date.getMinutes(),                 //分
            "s+": date.getSeconds(),                 //秒
            "q+": Math.floor((date.getMonth() + 3) / 3), //季度
            "S": date.getMilliseconds()             //毫秒
        };
        if (/(y+)/.test(fmt))
            fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt))
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    },
    //时间计算
    dateCalculate: function (date, num) {
        if (this.isBlank(date)) {
            date = new Date()
        }
        var a = new Date(date)
        a = a.valueOf()
        a = a - num * 24 * 60 * 60 * 1000
        a = new Date(a)
        return this.formatDate("yyyy-MM-dd hh:mm:ss", a)
    },
    /**
     * 距离当前时间差
     * @param date1
     * @returns {String}
     */
    timeToNow: function (date1) {
        //兼容微信浏览器,主动格式化时间字符串
        var arr1 = date1.split(" ");
        var sdate = arr1[0].split('-');
        var sTime = arr1[1].split(':');
        var date = new Date(sdate[0], sdate[1] - 1, sdate[2], sTime[0], sTime[1], sTime[2]);
        var setTime = new Date(date).getTime();
        var timer = null;
        var nowTime = new Date().getTime(),
            leftTime = 0,
            d = 0, h = 0, m = 0, s = 0;
        leftTime = Math.ceil((nowTime - setTime) / 1000);

        if (nowTime >= setTime) {
            d = ~~(leftTime / 86400);
            h = ~~(leftTime % 86400 / 3600);
            m = ~~(leftTime % 86400 % 3600 / 60);
            s = ~~(leftTime % 86400 % 3600 % 60);
        }
        if ((h + '').length == 1) {
            h = '0' + h;
        }
        if ((m + '').length == 1) {
            m = '0' + m;
        }
        if ((s + '').length == 1) {
            s = '0' + s;
        }
        return d + '天 ' + h + '时' + m + '分';
    }


}
//表单校验
var SOCOValidate = {
    //校验手机号码
    mobile: function (value, param) {
        return [/^(?:1\d\d)-?\d{5}(\d{3}|\*{3})$/.test(value), "手机号码不正确"]
    },
    email: function (value, param) {
        return [/^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/.test(value), "邮箱格式不正确"];
    },
    length: function (value, param) {
        var message = "长度必须在在" + param[0] + "与" + param[1] + "之间";
        if (value.length >= param[0] && value.length <= param[1]) {
            return [true]
        } else {
            return [false, message]
        }
    },
    QQ: function (value, param) {
        return [/^[1-9]\d{4,10}$/.test(value), "QQ号码不正确"];
    },
    ZIP: function (value, param) {
        return [/^[0-9]\d{5}$/.test(value), '邮政编码不存在'];
    },
}
var showMsg = {
    showWran: function (msg) {
        new $Msg({
            content: msg,
            type: "info",
            cancle: function () {
                return;
            },
            confirm: function () {
                return
            }
        });
    },
    showSuccess: function (msg) {
        new $Msg({
            content: msg,
            type: "success",
            cancle: function () {
                return;
            },
            confirm: function () {
                return
            }
        });
    },
    showError: function (msg) {
        new $Msg({
            content: msg,
            type: "wrong",
            cancle: function () {
                return;
            },
            confirm: function () {
                return
            }
        });
    }
}
