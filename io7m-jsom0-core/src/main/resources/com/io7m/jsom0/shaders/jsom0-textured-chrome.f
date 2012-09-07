#version 110

varying vec3 vertex_position;
varying vec3 vertex_normal;
varying vec2 vertex_uv;

uniform vec3 light_position;
uniform vec3 ambient;
uniform vec3 diffuse;
uniform vec4 specular;

uniform float shininess;
uniform float alpha;

uniform sampler2D texture;
uniform float     texture_alpha;

void
main (void)
{
  vec3 N = normalize(vertex_normal);
  vec3 V = normalize(vertex_position);
  vec3 R = reflect(V, N);
  vec3 L = normalize(light_position);

  vec4 o_ambient  = vec4(ambient, 1.0);
  vec4 o_diffuse  = vec4(diffuse, 1.0);
  vec4 o_specular = specular;

  vec4 r_ambient  = o_ambient;
  vec4 r_diffuse  = o_diffuse * max(dot(L, N), 0.0);
  vec4 r_specular = o_specular * pow(max(dot(R, L), 0.0), shininess);

  vec4 o_texture  = texture2D(texture, vertex_normal.xy);
  vec4 out_color  = r_ambient + r_diffuse + r_specular + (o_texture * texture_alpha);
  gl_FragColor    = vec4(out_color.rgb, alpha);
}
