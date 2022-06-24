import request from './request'

export const uploadApi = {
  aliyunUrl: process.env.BASE_API + "/v2/s/image/AliYunImgUpload",
  normalUrl: process.env.BASE_API + "/v2/s/image/commonImgUpload",
  aliyunFileUrl:'https://coin-excahege.oss-cn-chengdu.aliyuncs.com/',
  getPreUpload() {
    return request({
      url: `/admin/image/pre/upload`,
      method: 'get'
    })
  }
};
